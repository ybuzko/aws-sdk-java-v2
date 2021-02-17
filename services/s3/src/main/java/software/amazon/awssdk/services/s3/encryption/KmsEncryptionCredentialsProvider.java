package software.amazon.awssdk.services.s3.encryption;

import static software.amazon.awssdk.services.s3.encryption.ContentKeyEncryptionAlgorithm.KMS;
import static software.amazon.awssdk.services.s3.encryption.ContentKeyEncryptionAlgorithm.KMS_NO_CONTEXT;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import software.amazon.awssdk.services.s3.encryption.metadata.MetadataKey;
import software.amazon.awssdk.services.s3.encryption.model.ResolveKeyRequest;
import software.amazon.awssdk.services.s3.encryption.model.ResolveKeyResponse;

public class KmsEncryptionCredentialsProvider implements EncryptionCredentialsProvider {
    private static final MetadataKey<String> KEY_ID_METADATA = null; // TODO

    private final KmsClient client;
    private final String keyId;
    private final Map<String, String> context;

    private KmsEncryptionCredentialsProvider(String keyId, Map<String, String> context) {
        this.client = KmsClient.create();
        this.keyId = keyId;
        this.context = context;
    }

    public static KmsEncryptionCredentialsProvider create(String clientId, Map<String, String> context) {
        return new KmsEncryptionCredentialsProvider(clientId, context);
    }

    @Override
    public Set<ContentKeyEncryptionAlgorithm> supportedContentEncryptionKeyAlgorithms() {
        Set<ContentKeyEncryptionAlgorithm> result = new HashSet<>();
        result.add(KMS);
        result.add(ContentKeyEncryptionAlgorithm.KMS_NO_CONTEXT);
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
        boolean enforceContext = shouldEnforceContext(request);

        DecryptedSecretKey newKey = request.context().encryptionPolicy().contentKeyGenerator().generateKey();

        EncryptResponse encryptResponse =
            client.encrypt(r -> r.keyId(keyId)
                                 .encryptionContext(enforceContext ? context : null)
                                 .plaintext(SdkBytes.fromByteArrayUnsafe(newKey.key().getEncoded())));

        EncryptedSecretKey newEncryptedKey = EncryptedSecretKey.builder()
                                                               .content(encryptResponse.ciphertextBlob().asByteArrayUnsafe())
                                                               .algorithm(request.algorithm())
                                                               .build();

        return ResolveKeyResponse.builder()
                                 .secretKey(newKey)
                                 .putNewMetadata(KEY_ID_METADATA, keyId)
                                 .putNewMetadata(MetadataKey.ENCRYPTED_SECRET_KEY, newEncryptedKey)
                                 .putNewMetadata(MetadataKey.KEY_ALGORITHM, newKey.key().getAlgorithm())
                                 .build();
    }

    private ResolveKeyResponse decryptKey(ResolveKeyRequest request, EncryptedSecretKey secretKey) {
        boolean enforceContext = shouldEnforceContext(request);

        DecryptResponse decryptResponse =
            client.decrypt(r -> r.keyId(keyId)
                                 .encryptionContext(enforceContext ? context : null)
                                 .ciphertextBlob(SdkBytes.fromByteArrayUnsafe(secretKey.content())));

        String contentKeyType = request.context().metadata(MetadataKey.KEY_ALGORITHM);
        SecretKey key = new SecretKeySpec(decryptResponse.plaintext().asByteArrayUnsafe(), contentKeyType);

        return ResolveKeyResponse.builder()
                                 .secretKey(DecryptedSecretKey.create(key))
                                 .build();
    }

    private boolean shouldEnforceContext(ResolveKeyRequest request) {
        if (request.algorithm() == KMS) {
            return true;
        } else if (request.algorithm() == KMS_NO_CONTEXT) {
            return false;
        } else {
            throw new IllegalArgumentException("Unsupported algorithm: " + request.algorithm());
        }
    }
}
