package software.amazon.awssdk.services.s3.encryption;

import java.util.Collection;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.encryption.auth.EncryptionCredentials;
import software.amazon.awssdk.services.s3.encryption.content.DecryptObjectRequest;
import software.amazon.awssdk.services.s3.encryption.content.DecryptObjectResponse;
import software.amazon.awssdk.services.s3.encryption.content.EncryptObjectRequest;
import software.amazon.awssdk.services.s3.encryption.content.EncryptObjectResponse;
import software.amazon.awssdk.services.s3.encryption.internal.DefaultS3EncryptionRuntime;
import software.amazon.awssdk.utils.SdkAutoCloseable;

/**
 * A {@link S3EncryptionRuntime} has methods for encrypting and decrypting S3 object content.
 *
 * <p>The {@code S3EncryptionRuntime} is used by {@link S3Client} to encrypt put-object data streams, and decrypt get-object
 * data streams.
 */
public interface S3EncryptionRuntime extends SdkAutoCloseable {
    /**
     * Create an {@link S3EncryptionRuntime.Builder} using the provided encryption policy.
     *
     * The encryption policy defines which algorithms the {@code S3EncryptionRuntime} uses. The SDK provides multiple built-in
     * encryption policies. See {@link EncryptionPolicy} for which policy is right for your use-case.
     */
    static Builder builder(EncryptionPolicy encryptionPolicy) {
        return new DefaultS3EncryptionRuntime.Builder();
    }

    /**
     * Decrypt a synchronous stream of ciphertext data into a stream of plaintext data.
     */
    DecryptObjectResponse decryptObject(DecryptObjectRequest request);

    /**
     * Decrypt a synchronous stream of plaintext data into a stream of ciphertext data.
     */
    EncryptObjectResponse encryptObject(EncryptObjectRequest request);

    interface Builder {
        /**
         * Configure which {@link EncryptionCredentials} are used by this encryption client when encrypting and decrypting S3
         * object data.
         *
         * <p>This field is required.
         *
         * <p>See {@link EncryptionCredentials} for which credential options are provided with the SDK by default.
         */
        Builder encryptionCredentials(EncryptionCredentials encryptionCredentials);

        /**
         * Configure additional {@link EncryptionCredentials} that can be by this encryption client when decrypting S3 object
         * data.
         *
         * <p>This optional field allows you to configure additional credentials beyond those specified via
         * {@link #encryptionCredentials} to be used just when decrypting objects.
         *
         * <p>See {@link EncryptionCredentials} for which credential options are provided with the SDK by default.
         *
         * <p>This field particularly useful when migrating which encryption credentials you are using through the following
         * process:
         * <ol>
         *     <li>Add your new credentials as "read-only" credentials.</li>
         *     <li>Deploy your application so that any objects written using the new credentials can be read by your application.
         *     </li>
         *     <li>Move your new credentials to the "write" credentials, and your old credentials to be "read-only" credentials.
         *     </li>
         *     <li>Deploy your application, so that new objects are written using the new credentials.</li>
         *     <li>Re-encrypt your objects using the new credentials.</li>.
         *     <li>Remove your old credentials from being "read-only" credentials.</li>
         *     <li>Deploy your application, so that it will only use the new credentials.</li>
         * </ol>
         *
         * <p>This overrides any values set via other {@link #readEncryptionCredentials} methods.
         */
        Builder readEncryptionCredentials(EncryptionCredentials... readEncryptionCredentials);

        /**
         * Configure additional {@link EncryptionCredentials} that can be by this encryption client when decrypting S3 object
         * data.
         *
         * <p>See {@link #readEncryptionCredentials(EncryptionCredentials...)} for more information.
         *
         * <p>This overrides any values set via other {@link #readEncryptionCredentials} methods.
         */
        Builder readEncryptionCredentials(Collection<EncryptionCredentials> readEncryptionCredentials);

        /**
         * Create an {@link S3EncryptionRuntime} using the configuration on this client.
         */
        S3EncryptionRuntime build();
    }
}
