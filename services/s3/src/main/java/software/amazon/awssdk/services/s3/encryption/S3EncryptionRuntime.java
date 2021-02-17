package software.amazon.awssdk.services.s3.encryption;

import java.util.Collection;
import software.amazon.awssdk.services.s3.encryption.model.DecryptContentRequest;
import software.amazon.awssdk.services.s3.encryption.model.DecryptContentResponse;
import software.amazon.awssdk.services.s3.encryption.model.EncryptContentRequest;
import software.amazon.awssdk.services.s3.encryption.model.EncryptContentResponse;

public interface S3EncryptionRuntime {
    DecryptContentResponse decryptContent(DecryptContentRequest request);
    EncryptContentResponse encryptContent(EncryptContentRequest request);

    static Builder builder(EncryptionPolicy encryptionPolicy) {
        return null;
    }

    interface Builder {
        Builder additionalReadCredentialsProviders(EncryptionCredentialsProvider... additionalReadCredentialsProviders);
        Builder additionalReadCredentialsProviders(Collection<EncryptionCredentialsProvider> additionalReadCredentialsProviders);
        Builder encryptionCredentialsProvider(EncryptionCredentialsProvider readWriteCredentialsProvider);
        S3EncryptionRuntime build();
    }
}
