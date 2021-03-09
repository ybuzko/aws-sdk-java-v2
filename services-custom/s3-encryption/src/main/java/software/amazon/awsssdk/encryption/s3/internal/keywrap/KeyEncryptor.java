package software.amazon.awsssdk.encryption.s3.internal.keywrap;

import java.util.function.Consumer;
import software.amazon.awsssdk.encryption.s3.keywrap.DecryptKeyRequest;
import software.amazon.awsssdk.encryption.s3.keywrap.DecryptKeyResponse;
import software.amazon.awsssdk.encryption.s3.keywrap.EncryptKeyRequest;
import software.amazon.awsssdk.encryption.s3.keywrap.EncryptKeyResponse;

public interface KeyEncryptor {
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
