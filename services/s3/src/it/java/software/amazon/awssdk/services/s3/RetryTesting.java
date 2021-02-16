package software.amazon.awssdk.services.s3;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;
import software.amazon.awssdk.metrics.publishers.cloudwatch.CloudWatchMetricPublisher;

public class RetryTesting {
    @Test
    public void test() {
        MockServer mockServer = new MockServer(new MockServer.ThrottlingServerBehavior());
        mockServer.startServer();

        URI endpoint = URI.create("http://localhost:" + mockServer.getPort());

        S3AsyncClient client = S3AsyncClient.builder()
                                            .endpointOverride(endpoint)
                                            .overrideConfiguration(c -> c.addMetricPublisher(CloudWatchMetricPublisher.create()))
                                            .build();

        for (int i = 0; i < 1; ++i) {
            sendAtRate(client, 5, Duration.ofSeconds(30));
        }
        for (int i = 0; i < 1; ++i) {
            sendAtRate(client, 10, Duration.ofSeconds(30));
        }
        for (int i = 0; i < 1; ++i) {
            sendAtRate(client, 15, Duration.ofSeconds(30));
        }
        for (int i = 0; i < 1; ++i) {
            sendAtRate(client, 10, Duration.ofSeconds(30));
        }
        for (int i = 0; i < 1; ++i) {
            sendAtRate(client, 5, Duration.ofSeconds(30));
        }

        mockServer.stopServer();
    }

    public void sendAtRate(S3AsyncClient client, int tpsRate, Duration duration) {
        AtomicLong latencyMillis = new AtomicLong(0);
        AtomicLong count = new AtomicLong(0);
        AtomicLong failures = new AtomicLong(0);

        System.out.println("Sending " + tpsRate + " tps for " + duration.getSeconds() + " seconds.");
        Instant start = Instant.now();
        Instant end = start.plus(duration);

        int requestsThisSecond = 0;
        long trackedSecond = 0;

        while (Instant.now().isBefore(end)) {
            long currentSecond = Instant.now().getEpochSecond();

            if (trackedSecond != currentSecond) {
                requestsThisSecond = 0;
                trackedSecond = currentSecond;
            } else {
                ++requestsThisSecond;
                if (requestsThisSecond < tpsRate) {
                    Instant requestStart = Instant.now();
                    client.listBuckets().whenComplete((r, e) -> {
                        Duration requestLatency = Duration.between(requestStart, Instant.now());
                        count.addAndGet(requestLatency.toMillis());
                        count.incrementAndGet();
                        if (e != null) {
                            failures.incrementAndGet();
                        }
                    });
                } else {
                    Thread.yield();
                }
            }
        }

        System.out.println("| " + tpsRate +
                           " | " + duration.getSeconds() +
                           " | " + count +
                           " | " + latencyMillis.get() / count.get() + "ms" +
                           " | " + failures.get() +
                           " |");
    }

    private void printTableHeading() {
        System.out.println("|---------------------------------------------------------------|");
        System.out.println("| Sample TPS | Sample Duration | Count | Avg Latency | Failures |");
        System.out.println("|---------------------------------------------------------------|");
    }
}
