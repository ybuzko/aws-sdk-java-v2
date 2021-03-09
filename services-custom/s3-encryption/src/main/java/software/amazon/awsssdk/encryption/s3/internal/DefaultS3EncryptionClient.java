package software.amazon.awsssdk.encryption.s3.internal;

import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awsssdk.encryption.s3.EncryptionPolicy;
import software.amazon.awsssdk.encryption.s3.S3EncryptionClient;
import software.amazon.awsssdk.encryption.s3.S3EncryptionRuntime;
import software.amazon.awsssdk.encryption.s3.auth.EncryptionCredentials;
import software.amazon.awsssdk.encryption.s3.content.Content;
import software.amazon.awsssdk.encryption.s3.content.DecryptObjectRequest;
import software.amazon.awsssdk.encryption.s3.content.DecryptObjectResponse;
import software.amazon.awsssdk.encryption.s3.content.EncryptObjectRequest;
import software.amazon.awsssdk.encryption.s3.content.EncryptObjectResponse;
import software.amazon.awsssdk.encryption.s3.model.ReencryptRequest;
import software.amazon.awsssdk.encryption.s3.model.ReencryptResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public class DefaultS3EncryptionClient implements S3EncryptionClient {
    private final S3Client delegate;
    private final S3EncryptionRuntime runtime;

    public DefaultS3EncryptionClient(Builder builder) {
        this.delegate = Validate.paramNotNull(builder.s3Client, "s3Client");
        this.runtime = builder.runtimeBuilder.build();
    }

    public static Builder builder(EncryptionPolicy policy) {
        return new Builder(policy);
    }

    public static Builder builder(S3EncryptionRuntime runtime) {
        return new Builder(runtime);
    }

    @Override
    public String serviceName() {
        return delegate.serviceName();
    }

    @Override
    public void close() {
        runtime.close();
        delegate.close();
    }

    @Override
    public <ReturnT> ReturnT getObject(GetObjectRequest getObjectRequest,
                                       ResponseTransformer<GetObjectResponse, ReturnT> responseTransformer) {
        // TODO: Validate not ranged
        // TODO: Do we need to transform this request at all before sending it?

        return delegate.getObject(getObjectRequest, (getObjectResponse, encryptedStream) -> {
            DecryptObjectResponse decryptResponse =
                runtime.decryptObject(DecryptObjectRequest.builder()
                                                          .ciphertext(Content.create(encryptedStream, getObjectResponse.contentLength()))
                                                          .metadata(getObjectResponse.metadata())
                                                          .build());

            GetObjectResponse decryptedGetObjectResponse =
                getObjectResponse.copy(r -> r.contentLength(decryptResponse.plaintext().contentLength())
                                             .contentType(decryptResponse.contentType())
                                             .contentEncoding(decryptResponse.contentEncoding()));

            return responseTransformer.transform(decryptedGetObjectResponse,
                                                 AbortableInputStream.create(decryptResponse.plaintext().inputStream(),
                                                                             encryptedStream));
        });
    }

    @Override
    public PutObjectResponse putObject(PutObjectRequest putObjectRequest, RequestBody requestBody) {
        // TODO: Validate not ranged

        EncryptedContentStreamProvider provider = new EncryptedContentStreamProvider(putObjectRequest, requestBody);

        PutObjectRequest encryptedPutObjectRequest =
            putObjectRequest.copy(r -> r.metadata(provider.initialEncryptResponse.metadata())
                                        .contentLength(provider.initialEncryptResponse.ciphertext().contentLength())
                                        .contentType(provider.initialEncryptResponse.contentType())
                                        .contentEncoding(provider.initialEncryptResponse.contentEncoding()));

        // TODO: Do we need to transform this response at all before returning it?
        return delegate.putObject(encryptedPutObjectRequest,
                                  RequestBody.fromContentProvider(provider,
                                                                  encryptedPutObjectRequest.contentLength(),
                                                                  encryptedPutObjectRequest.contentType()));
    }

    @Override
    public ReencryptResponse reencryptObject(ReencryptRequest request) {
        ResponseInputStream<GetObjectResponse> getResult = getObject(r -> r.bucket(request.bucket()).key(request.key()));

        RequestBody requestBody = RequestBody.fromInputStream(getResult, getResult.response().contentLength());
        delegate.putObject(r -> r.bucket(request.bucket()).key(request.key()), requestBody);

        return ReencryptResponse.builder().build();
    }

    private class EncryptedContentStreamProvider implements ContentStreamProvider {
        private final AtomicInteger attempt = new AtomicInteger(0);

        private final EncryptObjectResponse initialEncryptResponse;
        private final PutObjectRequest putObjectRequest;
        private final RequestBody requestBody;

        public EncryptedContentStreamProvider(PutObjectRequest putObjectRequest,
                                              RequestBody requestBody) {
            this.putObjectRequest = putObjectRequest;
            this.requestBody = requestBody;
            this.initialEncryptResponse = createNewEncryptResponse();
        }

        @Override
        public InputStream newStream() {
            if (attempt.getAndIncrement() == 0) {
                return initialEncryptResponse.ciphertext().inputStream();
            } else {
                return createNewEncryptResponse().ciphertext().inputStream();
            }
        }

        private EncryptObjectResponse createNewEncryptResponse() {
            ContentStreamProvider contentStreamProvider = requestBody.contentStreamProvider();
            InputStream firstInputStream = contentStreamProvider.newStream();

            return runtime.encryptObject(EncryptObjectRequest.builder()
                                                             .plaintext(Content.create(firstInputStream,
                                                                                       putObjectRequest.contentLength()))
                                                             .metadata(putObjectRequest.metadata())
                                                             .contentType(putObjectRequest.contentType())
                                                             .contentEncoding(putObjectRequest.contentEncoding())
                                                             .build());
        }
    }

    private static class Builder implements S3EncryptionClient.Builder {
        private final S3EncryptionRuntime.Builder runtimeBuilder;
        private S3Client s3Client;

        public Builder(EncryptionPolicy policy) {
            runtimeBuilder = S3EncryptionRuntime.builder(policy);
        }

        public Builder(S3EncryptionRuntime runtime) {
            runtimeBuilder = runtime.toBuilder();
        }

        @Override
        public Builder s3Client(S3Client client) {
            this.s3Client = client;
            return this;
        }

        @Override
        public Builder encryptionCredentials(EncryptionCredentials encryptionCredentials) {
            runtimeBuilder.encryptionCredentials(encryptionCredentials);
            return this;
        }

        @Override
        public Builder readEncryptionCredentials(EncryptionCredentials... readEncryptionCredentials) {
            runtimeBuilder.readEncryptionCredentials(readEncryptionCredentials);
            return this;
        }

        @Override
        public Builder readEncryptionCredentials(Collection<EncryptionCredentials> readEncryptionCredentials) {
            runtimeBuilder.readEncryptionCredentials(readEncryptionCredentials);
            return this;
        }

        @Override
        public S3EncryptionClient build() {
            return new DefaultS3EncryptionClient(this);
        }
    }
}
