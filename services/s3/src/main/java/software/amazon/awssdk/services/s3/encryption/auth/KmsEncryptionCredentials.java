package software.amazon.awssdk.services.s3.encryption.auth;

import static software.amazon.awssdk.services.s3.encryption.keywrap.KeyWrapAlgorithm.KMS;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.spec.SecretKeySpec;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import software.amazon.awssdk.services.s3.encryption.keywrap.DecryptKeyRequest;
import software.amazon.awssdk.services.s3.encryption.keywrap.DecryptKeyResponse;
import software.amazon.awssdk.services.s3.encryption.keywrap.EncryptKeyRequest;
import software.amazon.awssdk.services.s3.encryption.keywrap.EncryptKeyResponse;
import software.amazon.awssdk.services.s3.encryption.keywrap.KeyWrapAlgorithm;

public class KmsEncryptionCredentials implements EncryptionCredentials {
    private final KmsClient kmsClient;
    private final String keyId;
    private final Map<String, String> context;

    private KmsEncryptionCredentials(String keyId, Map<String, String> context) {
        this.kmsClient = KmsClient.create();
        this.keyId = keyId;
        this.context = context;
    }

    public static KmsEncryptionCredentials create(String clientId, Map<String, String> context) {
        return new KmsEncryptionCredentials(clientId, context);
    }

    @Override
    public String id() {
        return keyId;
    }

    @Override
    public Set<KeyWrapAlgorithm> supportedKeyWrapAlgorithms() {
        Set<KeyWrapAlgorithm> result = new HashSet<>();
        if (context == null) {
            result.add(KeyWrapAlgorithm.KMS_NO_CONTEXT);
        } else {
            result.add(KMS);
        }
        return result;
    }

    @Override
    public EncryptKeyResponse encryptKey(EncryptKeyRequest request) {
        EncryptResponse encryptResponse =
            kmsClient.encrypt(r -> r.keyId(keyId)
                                    .applyMutation(this::addContextIfConfigured)
                                    .plaintext(SdkBytes.fromByteArrayUnsafe(request.key().getEncoded())));

        return EncryptKeyResponse.builder()
                                 .key(k -> k.content(encryptResponse.ciphertextBlob())
                                            .keyWrapAlgorithm(KeyWrapAlgorithm.KMS)
                                            .keyAlgorithm(request.key().getAlgorithm()))
                                 .build();
    }

    @Override
    public DecryptKeyResponse decryptKey(DecryptKeyRequest request) {
        DecryptResponse decryptResponse =
            kmsClient.decrypt(r -> r.keyId(keyId)
                                    .applyMutation(this::addContextIfConfigured)
                                    .ciphertextBlob(request.key().content()));

        return DecryptKeyResponse.builder()
                                 .key(new SecretKeySpec(decryptResponse.plaintext().asByteArrayUnsafe(),
                                                        request.key().keyAlgorithm()))
                                 .build();
    }

    private void addContextIfConfigured(EncryptRequest.Builder builder) {
        if (context != null) {
            builder.encryptionContext(context);
        }
    }

    private void addContextIfConfigured(DecryptRequest.Builder builder) {
        if (context != null) {
            builder.encryptionContext(context);
        }
    }
}
