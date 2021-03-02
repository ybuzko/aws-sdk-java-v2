package software.amazon.awssdk.services.s3.encryption;

import java.util.Collection;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.encryption.auth.EncryptionCredentials;
import software.amazon.awssdk.services.s3.encryption.internal.DefaultS3EncryptionClient;
import software.amazon.awssdk.services.s3.encryption.model.ReencryptRequest;
import software.amazon.awssdk.services.s3.encryption.model.ReencryptResponse;

/**
 * An implementation of {@link S3Client} that encrypts and decrypts the S3 object content when it is uploaded or
 * downloaded from S3.
 *
 * <p>This is created using {@link #builder(EncryptionPolicy)} and destroyed using {@link #close()}.
 *
 * <p>Tips:
 * <ol>
 *     <li>{@code S3EncryptionClient} is thread-safe, and moderately expensive to create. You should create it once and use it
 *     throughout your application.</li>
 *     <li>{@code S3EncryptionClient} uses an {@link S3EncryptionRuntime} and {@link S3Client} "under-the-hood". Check out those
 *     classes for more configuration options. You can configure those classes via the {@link S3EncryptionClient.Builder}</li>
 * </ol>
 */
@SdkPublicApi
@Immutable
@ThreadSafe
public interface S3EncryptionClient extends S3Client {
    /**
     * Create an {@link S3EncryptionClient.Builder} using the provided encryption policy.
     *
     * The encryption policy defines which algorithms the {@code S3EncryptionClient} uses. The SDK provides multiple built-in
     * encryption policies. See {@link EncryptionPolicy} for which policy is right for your use-case.
     */
    static Builder builder(EncryptionPolicy encryptionPolicy) {
        return new DefaultS3EncryptionClient.Builder();
    }

    /**
     * Re-encrypt an object that is stored in S3 by downloading it and re-uploading it.
     *
     * <p>This is useful for key rotation, which is described in more detail in
     * {@link Builder#readEncryptionCredentials(Collection)}.
     *
     * <p>This is equivalent to calling {@link #getObject} followed by {@link #putObject}, and can throw any exceptions thrown
     * by those operations.
     */
    ReencryptResponse reencryptObject(ReencryptRequest request);

    /**
     * Created via {@link #builder(EncryptionPolicy)}, this object can configure and create {@link S3EncryptionClient}s.
     */
    interface Builder {
        /**
         * Configure the {@link S3Client} implementation that is used by this encryption client for communication with S3.
         *
         * <p>This optional field allows configuring the SDK's communication settings, like which AWS region to connect to,
         * connection pool sizes, and timeouts.
         *
         * <p>If you specify this value, {@link S3EncryptionClient#close()} will not close the {@code S3Client} you've provided,
         * so make sure to close the underlying client when you're done with it.
         *
         * <p>If not specified, {@link S3Client#create()}} will used.
         */
        Builder s3Client(S3Client client);

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
         *     <li>Re-encrypt your objects using the new credentials via {@link #reencryptObject}</li>.
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
         * Create an {@link S3EncryptionClient} using the configuration on this builder.
         */
        S3EncryptionClient build();
    }
}
