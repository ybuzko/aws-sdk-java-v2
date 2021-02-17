package software.amazon.awssdk.services.s3.encryption.internal.keywrap;

import java.util.Map;
import javax.crypto.spec.SecretKeySpec;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import software.amazon.awssdk.services.s3.encryption.keywrap.DecryptKeyRequest;
import software.amazon.awssdk.services.s3.encryption.keywrap.DecryptKeyResponse;
import software.amazon.awssdk.services.s3.encryption.keywrap.EncryptKeyRequest;
import software.amazon.awssdk.services.s3.encryption.keywrap.EncryptKeyResponse;
import software.amazon.awssdk.services.s3.encryption.keywrap.KeyEncryptor;
import software.amazon.awssdk.services.s3.encryption.keywrap.KeyWrapAlgorithm;
import software.amazon.awssdk.services.s3.encryption.metadata.MetadataKey;

@SdkInternalApi
public class KmsKeyEncryptor implements KeyEncryptor {
    static final MetadataKey<String> KEY_ID_METADATA = null; // TODO

    private final KmsClient kmsClient;
    private final String keyId;
    private final Map<String, String> kmsContext;

    public KmsKeyEncryptor(KmsClient kmsClient,
                           String keyId,
                           Map<String, String> kmsContext) {
        this.kmsClient = kmsClient;
        this.keyId = keyId;
        this.kmsContext = kmsContext;
    }

    @Override
    public KeyWrapAlgorithm algorithm() {
        return KeyWrapAlgorithm.KMS;
    }

    @Override
    public EncryptKeyResponse encryptKey(EncryptKeyRequest request) {
        EncryptResponse encryptResponse =
            kmsClient.encrypt(r -> r.keyId(keyId)
                                    .encryptionContext(kmsContext)
                                    .plaintext(SdkBytes.fromByteArrayUnsafe(request.key().getEncoded())));

        return EncryptKeyResponse.builder()
                                 .key(k -> k.content(encryptResponse.ciphertextBlob())
                                            .keyWrapAlgorithm(algorithm())
                                            .keyAlgorithm(request.key().getAlgorithm()))
                                 .putNewMetadata(KEY_ID_METADATA, keyId)
                                 .build();
    }

    @Override
    public DecryptKeyResponse decryptKey(DecryptKeyRequest request) {
        DecryptResponse decryptResponse =
            kmsClient.decrypt(r -> r.keyId(request.context().metadata(KEY_ID_METADATA))
                                    .encryptionContext(kmsContext)
                                    .ciphertextBlob(request.key().content()));

        return DecryptKeyResponse.builder()
                                 .key(new SecretKeySpec(decryptResponse.plaintext().asByteArrayUnsafe(),
                                                        request.key().keyAlgorithm()))
                                 .build();
    }
}
