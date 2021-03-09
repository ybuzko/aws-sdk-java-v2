package software.amazon.awsssdk.encryption.s3.auth;

import java.util.Set;
import java.util.function.Consumer;
import software.amazon.awsssdk.encryption.s3.keywrap.DecryptKeyRequest;
import software.amazon.awsssdk.encryption.s3.keywrap.DecryptKeyResponse;
import software.amazon.awsssdk.encryption.s3.keywrap.EncryptKeyRequest;
import software.amazon.awsssdk.encryption.s3.keywrap.EncryptKeyResponse;
import software.amazon.awsssdk.encryption.s3.keywrap.KeyWrapAlgorithm;

/**
 * A set of {@link EncryptionCredentials} defines the means by which encryption credentials are managed by the SDK.
 *
 * <p>Several {@code EncryptionCredentials} implementations are provided by the SDK:
 * <ul>
 *     <ol>{@link KmsEncryptionCredentials}, which use keys managed by AWS KMS.</ol>
 *     <ol>{@link SymmetricEncryptionCredentials}, which use a symmetric key managed by the user.</ol>
 *     <ol>{@link AsymmetricEncryptionCredentials}, which use an asymmetric key pair managed by the user.</ol>
 * </ul>
 *
 * <p>Implementation Note: The {@code EncryptionCredentials} are not used to encrypt object content. The credentials are used to
 * encrypt a symmetric content encryption key, which is what is actually used to encrypt the object content.
 */
public interface EncryptionCredentials {
    /**
     * A unique ID for these encryption credentials. They should uniquely identify the credentials, so that when credentials are
     * rotated they will use a different ID.
     *
     * <p>This ID is stored with the object metadata, and is used to identify which encryption credentials should be used for
     * decrypting the object.
     */
    String id();

    /**
     * Which key wrap algorithms are supported by this {@link EncryptionCredentials} implementation.
     */
    Set<KeyWrapAlgorithm> supportedKeyWrapAlgorithms();

    /**
     * Encrypt a content key using one of the supported key wrap algorithms.
     */
    EncryptKeyResponse encryptKey(EncryptKeyRequest request);

    /**
     * Encrypt a content key using one of the supported key wrap algorithms.
     *
     * <p>This is a convenience method for calling {@link #encryptKey(EncryptKeyRequest)} without needing to call
     * {@code EncryptKeyRequest.builder()} or {@code build()}.
     */
    default EncryptKeyResponse encryptKey(Consumer<EncryptKeyRequest.Builder> request) {
        EncryptKeyRequest.Builder builder = EncryptKeyRequest.builder();
        request.accept(builder);
        return encryptKey(builder.build());
    }

    /**
     * Decrypt a content key using one of the supported key wrap algorithms.
     */
    DecryptKeyResponse decryptKey(DecryptKeyRequest request);

    /**
     * Decrypt a content key using one of the supported key wrap algorithms.
     *
     * <p>This is a convenience method for calling {@link #decryptKey(DecryptKeyRequest)} without needing to call
     * {@code DecryptKeyRequest.builder()} or {@code build()}.
     */
    default DecryptKeyResponse decryptKey(Consumer<DecryptKeyRequest.Builder> request) {
        DecryptKeyRequest.Builder builder = DecryptKeyRequest.builder();
        request.accept(builder);
        return decryptKey(builder.build());
    }
}
