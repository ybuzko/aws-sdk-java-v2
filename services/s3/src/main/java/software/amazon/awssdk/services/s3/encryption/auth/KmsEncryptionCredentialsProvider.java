package software.amazon.awssdk.services.s3.encryption.auth;

import static software.amazon.awssdk.services.s3.encryption.keywrap.KeyWrapAlgorithm.KMS;
import static software.amazon.awssdk.services.s3.encryption.keywrap.KeyWrapAlgorithm.KMS_NO_CONTEXT;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import software.amazon.awssdk.services.s3.encryption.internal.keywrap.KmsKeyEncryptor;
import software.amazon.awssdk.services.s3.encryption.internal.keywrap.KmsNoContextKeyEncryptor;
import software.amazon.awssdk.services.s3.encryption.keywrap.EncryptedSecretKey;
import software.amazon.awssdk.services.s3.encryption.keywrap.KeyEncryptor;
import software.amazon.awssdk.services.s3.encryption.keywrap.KeyWrapAlgorithm;
import software.amazon.awssdk.services.s3.encryption.metadata.MetadataKey;

public class KmsEncryptionCredentialsProvider implements EncryptionCredentialsProvider {
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
    public Set<KeyWrapAlgorithm> supportedKeyWrapAlgorithms() {
        Set<KeyWrapAlgorithm> result = new HashSet<>();
        result.add(KMS);
        result.add(KeyWrapAlgorithm.KMS_NO_CONTEXT);
        return result;
    }

    @Override
    public KeyEncryptor createKeyEncryptor(KeyWrapAlgorithm algorithm) {
        if (algorithm == KMS) {
            return new KmsKeyEncryptor(client, keyId, context);
        } else if (algorithm == KMS_NO_CONTEXT) {
            return new KmsNoContextKeyEncryptor(client, keyId);
        } else {
            throw new IllegalArgumentException("Unsupported key-wrap algorithm: " + algorithm);
        }
    }

    //    @Override
//    public ResolveKeyResponse resolveKey(ResolveKeyRequest request) {
//        EncryptedSecretKey secretKey = request.context().metadata(MetadataKey.ENCRYPTED_SECRET_KEY);
//
//        if (secretKey == null) {
//            return createNewKey(request);
//        }
//
//        return decryptKey(request, secretKey);
//
//    }
//
//    private ResolveKeyResponse createNewKey(ResolveKeyRequest request) {
//        boolean enforceContext = shouldEnforceContext(request);
//
//        SecretKey newKey = request.context().encryptionPolicy().keyGeneratorProvider().createKeyGenerator().generateKey();
//
//        request.algorithm().createEncryptor().encryptKey()
//
//        EncryptedSecretKey newEncryptedKey = EncryptedSecretKey.builder()
//                                                               .content(encryptResponse.ciphertextBlob().asByteArrayUnsafe())
//                                                               .keyWrapAlgorithm(request.algorithm())
//                                                               .build();
//
//        return ResolveKeyResponse.builder()
//                                 .secretKey(newKey)
//                                 .putNewMetadata(KEY_ID_METADATA, keyId)
//                                 .putNewMetadata(MetadataKey.ENCRYPTED_SECRET_KEY, newEncryptedKey)
//                                 .putNewMetadata(MetadataKey.KEY_ALGORITHM, newKey.getAlgorithm())
//                                 .build();
//    }
//
//    private ResolveKeyResponse decryptKey(ResolveKeyRequest request, EncryptedSecretKey secretKey) {
//        boolean enforceContext = shouldEnforceContext(request);
//
//        DecryptResponse decryptResponse =
//            client.decrypt(r -> r.keyId(keyId)
//                                 .encryptionContext(enforceContext ? context : null)
//                                 .ciphertextBlob(SdkBytes.fromByteArrayUnsafe(secretKey.content())));
//
//        String contentKeyType = request.context().metadata(MetadataKey.KEY_ALGORITHM);
//        SecretKey key = new SecretKeySpec(decryptResponse.plaintext().asByteArrayUnsafe(), contentKeyType);
//
//        return ResolveKeyResponse.builder()
//                                 .secretKey(key)
//                                 .build();
//    }
//
//    private boolean shouldEnforceContext(ResolveKeyRequest request) {
//        if (request.algorithm() == KMS) {
//            return true;
//        } else if (request.algorithm() == KMS_NO_CONTEXT) {
//            return false;
//        } else {
//            throw new IllegalArgumentException("Unsupported algorithm: " + request.algorithm());
//        }
//    }
}
