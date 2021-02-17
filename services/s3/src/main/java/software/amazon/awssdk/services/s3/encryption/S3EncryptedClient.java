package software.amazon.awssdk.services.s3.encryption;

import java.util.Collection;
import java.util.function.Consumer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.encryption.auth.EncryptionCredentialsProvider;
import software.amazon.awssdk.services.s3.encryption.model.ReencryptResponse;
import software.amazon.awssdk.services.s3.encryption.model.ReencryptRequest;

public interface S3EncryptedClient extends S3Client {
    static Builder builder(EncryptionPolicy encryptionPolicy) {
        return null;
    }

    ReencryptResponse reencryptObject(ReencryptRequest request);

    default ReencryptResponse reencryptObject(Consumer<ReencryptRequest.Builder> request) {
        ReencryptRequest.Builder builder = ReencryptRequest.builder();
        request.accept(builder);
        return reencryptObject(builder.build());
    }

    interface Builder {
        Builder s3Client(S3Client client);
        Builder readEncryptionCredentialsProviders(EncryptionCredentialsProvider... additionalReadCredentialsProviders);
        Builder readEncryptionCredentialsProviders(Collection<EncryptionCredentialsProvider> additionalReadCredentialsProviders);
        Builder encryptionCredentialsProvider(EncryptionCredentialsProvider readWriteCredentialsProvider);
        S3EncryptedClient build();
    }
}
