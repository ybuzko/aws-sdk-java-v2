package software.amazon.awssdk.services.s3.encryption;

import java.util.Collection;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.encryption.auth.EncryptionCredentials;
import software.amazon.awssdk.services.s3.encryption.content.DecryptObjectRequest;
import software.amazon.awssdk.services.s3.encryption.content.DecryptObjectResponse;
import software.amazon.awssdk.services.s3.encryption.content.EncryptObjectRequest;
import software.amazon.awssdk.services.s3.encryption.content.EncryptObjectResponse;
import software.amazon.awssdk.utils.SdkAutoCloseable;

/**
 * A {@link S3EncryptionRuntime} has methods for encrypting and decrypting S3 object content.
 *
 * <p>The {@code S3EncryptionRuntime} is used by {@link S3Client} to encrypt put-object data streams, and decrypt get-object
 * data streams.
 */
public interface S3EncryptionRuntime extends SdkAutoCloseable {
    DecryptObjectResponse decryptObject(DecryptObjectRequest request);
    EncryptObjectResponse encryptObject(EncryptObjectRequest request);

    static Builder builder(EncryptionPolicy encryptionPolicy) {
        return null;
    }

    interface Builder {
        Builder readEncryptionCredentials(EncryptionCredentials... readEncryptionCredentials);
        Builder readEncryptionCredentials(Collection<EncryptionCredentials> readEncryptionCredentials);
        Builder encryptionCredentials(EncryptionCredentials encryptionCredentials);
        S3EncryptionRuntime build();
    }
}
