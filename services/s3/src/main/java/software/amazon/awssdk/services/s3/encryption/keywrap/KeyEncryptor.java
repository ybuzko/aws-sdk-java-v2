package software.amazon.awssdk.services.s3.encryption.keywrap;

import java.util.function.Consumer;

public interface KeyEncryptor {
    KeyWrapAlgorithm algorithm();
    
    EncryptKeyResponse encryptKey(EncryptKeyRequest request);

    default EncryptKeyResponse encryptKey(Consumer<EncryptKeyRequest.Builder> request) {
        EncryptKeyRequest.Builder builder = EncryptKeyRequest.builder();
        request.accept(builder);
        return encryptKey(builder.build());
    }

    DecryptKeyResponse decryptKey(DecryptKeyRequest request);

    default DecryptKeyResponse decryptKey(Consumer<DecryptKeyRequest.Builder> request) {
        DecryptKeyRequest.Builder builder = DecryptKeyRequest.builder();
        request.accept(builder);
        return decryptKey(builder.build());
    }
}
