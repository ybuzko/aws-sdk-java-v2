package software.amazon.awssdk.services.s3.encryption.auth;

import java.util.HashSet;
import java.util.Set;
import javax.crypto.SecretKey;
import software.amazon.awssdk.services.s3.encryption.keywrap.EncryptedSecretKey;
import software.amazon.awssdk.services.s3.encryption.keywrap.KeyWrapAlgorithm;
import software.amazon.awssdk.services.s3.encryption.metadata.MetadataKey;

public class StaticSymmetricCredentialsProvider implements EncryptionCredentialsProvider {
    private final SecretKey secretKey;

    private StaticSymmetricCredentialsProvider(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public StaticSymmetricCredentialsProvider create(SecretKey secretKey) {
        return new StaticSymmetricCredentialsProvider(secretKey);
    }

    @Override
    public Set<KeyWrapAlgorithm> supportedKeyWrapAlgorithms() {
        Set<KeyWrapAlgorithm> result = new HashSet<>();
        result.add(KeyWrapAlgorithm.AES_GCM);
        result.add(KeyWrapAlgorithm.AES);
        return result;
    }

    @Override
    public ResolveKeyResponse resolveKey(ResolveKeyRequest request) {
        EncryptedSecretKey secretKey = request.context().metadata(MetadataKey.ENCRYPTED_SECRET_KEY);

        if (secretKey == null) {
            return createNewKey(request);
        }

        return decryptKey(request, secretKey);
    }

    private ResolveKeyResponse createNewKey(ResolveKeyRequest request) {
        SecretKey newKey = request.context().encryptionPolicy().keyGeneratorProvider().createKeyGenerator().generateKey();

        request.algorithm();

        return null;
    }

    private ResolveKeyResponse decryptKey(ResolveKeyRequest request, EncryptedSecretKey secretKey) {
        return null;
    }
}
