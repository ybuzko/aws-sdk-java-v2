package software.amazon.awssdk.services.s3.encryption.auth;

import java.util.Set;
import java.util.function.Consumer;
import software.amazon.awssdk.services.s3.encryption.keywrap.DecryptKeyRequest;
import software.amazon.awssdk.services.s3.encryption.keywrap.DecryptKeyResponse;
import software.amazon.awssdk.services.s3.encryption.keywrap.EncryptKeyRequest;
import software.amazon.awssdk.services.s3.encryption.keywrap.EncryptKeyResponse;
import software.amazon.awssdk.services.s3.encryption.keywrap.KeyWrapAlgorithm;

public interface EncryptionCredentials {
    String id();

    Set<KeyWrapAlgorithm> supportedKeyWrapAlgorithms();

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
