package software.amazon.awssdk.services.dynamodb;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class RetryTesting {
    public static void main(String... args) throws InterruptedException {
        DynamoDbAsyncClient client = DynamoDbAsyncClient.builder()
                                                        .overrideConfiguration(c -> c.retryPolicy(RetryMode.STANDARD))
                                                        .build();

        client.putItem(r -> r.tableName("retry-testing").item(Collections.singletonMap("id", AttributeValue.builder().s("foo").build())));

        try (Sender sender = new Sender()) {
            sender.start(client);

            printTableHeading();

            int lowerBound = 5;
            int upperBound = 40;
            int delta = 5;
            Duration runDuration = Duration.ofMinutes(2);

            for (int i = lowerBound; i <= upperBound; i += delta) {
                sender.setConcurrentRequests(i);
                Thread.sleep(runDuration.toMillis());
            }

            for (int i = upperBound - delta; i >= lowerBound; i -= delta) {
                sender.setConcurrentRequests(i);
                Thread.sleep(runDuration.toMillis());
            }

            sender.setConcurrentRequests(0);
        }
    }

    private static void printTableHeading() {
        System.out.println("Concurrency,Sample Duration (s),TPS,Count,p99 Latency (ms),Failures");
    }

    private static class Sender implements AutoCloseable {
        private final Semaphore semaphore = new Semaphore(0);
        private final AtomicInteger allowedConcurrency = new AtomicInteger(0);
        private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        private final AtomicReference<Metrics> metrics = new AtomicReference<>(new Metrics());

        private boolean running = false;

        private synchronized void setConcurrentRequests(int allowedConcurrency) throws InterruptedException {
            int currentAllowedConcurrency = this.allowedConcurrency.get();

            if (currentAllowedConcurrency > allowedConcurrency) {
                semaphore.acquire(currentAllowedConcurrency - allowedConcurrency);
            } else if (currentAllowedConcurrency < allowedConcurrency){
                semaphore.release(allowedConcurrency - currentAllowedConcurrency);
            } else {
                throw new IllegalArgumentException();
            }

            this.allowedConcurrency.set(allowedConcurrency);

            Metrics metrics = this.metrics.getAndSet(new Metrics());
            metrics.lock();

            if (metrics.count() > 0) {
                System.out.println(currentAllowedConcurrency + "," +
                                   metrics.duration().getSeconds() + "," +
                                   metrics.tps() + "," +
                                   metrics.count() + "," +
                                   metrics.p99LatencyInMillis() + "," +
                                   metrics.failures());
            }
        }

        public synchronized void start(DynamoDbAsyncClient client) {
            if (!running) {
                running = true;
                executor.submit(() -> sendRequests(client));
            }
        }

        private void sendRequests(DynamoDbAsyncClient client) {
            try {
                while (running) {
                    semaphore.acquire();

                    if (!running) {
                        break;
                    }

                    Instant requestStart = Instant.now();
                    client.getItem(r -> r.tableName("retry-testing")
                                         .key(Collections.singletonMap("id", AttributeValue.builder()
                                                                                           .s("foo")
                                                                                           .build())))
                          .whenComplete((r, e) -> {
                              semaphore.release();
                              Metrics metrics = this.metrics.get();
                              metrics.recordLatency(Duration.between(requestStart, Instant.now()));
                              if (e != null) {
                                  metrics.recordFailure();
                              } else {
                                  metrics.recordSuccess();
                              }
                          });
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public synchronized void close() {
            running = false;
            semaphore.release(this.allowedConcurrency.get());
            executor.shutdown();
        }
    }

    private static class Metrics {
        private final Instant start = Instant.now();
        private final AtomicLong successes = new AtomicLong(0);
        private final AtomicLong failures = new AtomicLong(0);
        private final List<Duration> latencies = Collections.synchronizedList(new ArrayList<>());

        private volatile boolean locked = false;
        private volatile Instant end = start;

        private void recordLatency(Duration latency) {
            if (!locked) {
                latencies.add(latency);
            }
        }

        private void recordSuccess() {
            if (!locked) {
                successes.incrementAndGet();
                end = Instant.now();
            }
        }

        private void recordFailure() {
            if (!locked) {
                failures.incrementAndGet();
                end = Instant.now();
            }
        }

        private void lock() {
            locked = true;
        }

        private Duration duration() {
            return Duration.between(start, end);
        }

        public long p99LatencyInMillis() {
            latencies.sort(Duration::compareTo);
            return latencies.get(latencies.size() - latencies.size() / 100).toMillis();
        }

        public long tps() {
            return count() / duration().getSeconds();
        }

        public long successes() {
            return successes.get();
        }

        public long failures() {
            return failures.get();
        }

        public long count() {
            return successes() + failures();
        }
    }
}
