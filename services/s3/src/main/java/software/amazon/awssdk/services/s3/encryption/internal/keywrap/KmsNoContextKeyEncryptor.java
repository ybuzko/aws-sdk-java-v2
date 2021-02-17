package software.amazon.awssdk.services.s3.encryption.internal.keywrap;

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
public class KmsNoContextKeyEncryptor implements KeyEncryptor {
    private final KmsClient kmsClient;
    private final String keyId;

    public KmsNoContextKeyEncryptor(KmsClient kmsClient, String keyId) {
        this.kmsClient = kmsClient;
        this.keyId = keyId;
    }

    @Override
    public KeyWrapAlgorithm algorithm() {
        return KeyWrapAlgorithm.KMS_NO_CONTEXT;
    }

    @Override
    public EncryptKeyResponse encryptKey(EncryptKeyRequest request) {
        EncryptResponse encryptResponse =
            kmsClient.encrypt(r -> r.keyId(keyId)
                                    .plaintext(SdkBytes.fromByteArrayUnsafe(request.key().getEncoded())));

        return EncryptKeyResponse.builder()
                                 .key(k -> k.content(encryptResponse.ciphertextBlob())
                                            .keyWrapAlgorithm(algorithm())
                                            .keyAlgorithm(request.key().getAlgorithm()))
                                 .putNewMetadata(KmsKeyEncryptor.KEY_ID_METADATA, keyId)
                                 .build();
    }

    @Override
    public DecryptKeyResponse decryptKey(DecryptKeyRequest request) {
        DecryptResponse decryptResponse =
            kmsClient.decrypt(r -> r.keyId(request.context().metadata(KmsKeyEncryptor.KEY_ID_METADATA))
                                    .ciphertextBlob(request.key().content()));

        return DecryptKeyResponse.builder()
                                 .key(new SecretKeySpec(decryptResponse.plaintext().asByteArrayUnsafe(),
                                                        request.key().keyAlgorithm()))
                                 .build();
    }
}
