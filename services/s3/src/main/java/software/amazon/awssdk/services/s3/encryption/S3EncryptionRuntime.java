package software.amazon.awssdk.services.s3.encryption;

import java.util.Collection;
import software.amazon.awssdk.services.s3.encryption.auth.EncryptionCredentialsProvider;
import software.amazon.awssdk.services.s3.encryption.content.DecryptContentRequest;
import software.amazon.awssdk.services.s3.encryption.content.DecryptContentResponse;
import software.amazon.awssdk.services.s3.encryption.content.EncryptContentRequest;
import software.amazon.awssdk.services.s3.encryption.content.EncryptContentResponse;

public interface S3EncryptionRuntime {
    DecryptContentResponse decryptContent(DecryptContentRequest request);
    EncryptContentResponse encryptContent(EncryptContentRequest request);

    static Builder builder(EncryptionPolicy encryptionPolicy) {
        return null;
    }

    interface Builder {
        Builder readEncryptionCredentialsProviders(EncryptionCredentialsProvider... additionalReadCredentialsProviders);
        Builder readEncryptionCredentialsProviders(Collection<EncryptionCredentialsProvider> additionalReadCredentialsProviders);
        Builder encryptionCredentialsProvider(EncryptionCredentialsProvider readWriteCredentialsProvider);
        S3EncryptionRuntime build();
    }
}
