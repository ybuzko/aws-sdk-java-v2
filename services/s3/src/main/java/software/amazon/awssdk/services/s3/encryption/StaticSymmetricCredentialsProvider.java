package software.amazon.awssdk.services.s3.encryption;

import java.util.HashSet;
import java.util.Set;
import javax.crypto.SecretKey;
import software.amazon.awssdk.services.s3.encryption.metadata.MetadataKey;
import software.amazon.awssdk.services.s3.encryption.model.ResolveKeyRequest;
import software.amazon.awssdk.services.s3.encryption.model.ResolveKeyResponse;

public class StaticSymmetricCredentialsProvider implements EncryptionCredentialsProvider {
    private final SecretKey secretKey;

    private StaticSymmetricCredentialsProvider(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public StaticSymmetricCredentialsProvider create(SecretKey secretKey) {
        return new StaticSymmetricCredentialsProvider(secretKey);
    }

    @Override
    public Set<ContentKeyEncryptionAlgorithm> supportedContentEncryptionKeyAlgorithms() {
        Set<ContentKeyEncryptionAlgorithm> result = new HashSet<>();
        result.add(ContentKeyEncryptionAlgorithm.AES_GCM);
        result.add(ContentKeyEncryptionAlgorithm.AES);
        return result;
    }

    @Override
    public ResolveKeyResponse resolveContentKey(ResolveKeyRequest request) {
        EncryptedSecretKey secretKey = request.context().metadata(MetadataKey.ENCRYPTED_SECRET_KEY);

        if (secretKey == null) {
            return createNewKey(request);
        }

        return decryptKey(request, secretKey);
    }

    private ResolveKeyResponse createNewKey(ResolveKeyRequest request) {
        DecryptedSecretKey newKey = request.context().encryptionPolicy().contentKeyGenerator().generateKey();

        return null;
    }

    private ResolveKeyResponse decryptKey(ResolveKeyRequest request, EncryptedSecretKey secretKey) {
        return null;
    }
}
