package software.amazon.awsssdk.encryption.s3.keywrap;

import javax.crypto.SecretKey;
import software.amazon.awsssdk.encryption.s3.EncryptionContext;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface EncryptKeyRequest extends ToCopyableBuilder<EncryptKeyRequest.Builder, EncryptKeyRequest> {
    SecretKey key();
    KeyWrapAlgorithm keyWrapAlgorithm();
    EncryptionContext context();

    static Builder builder() {
        return null;
    }

    interface Builder extends CopyableBuilder<Builder, EncryptKeyRequest> {
        Builder key(SecretKey key);
        Builder keyWrapAlgorithm(KeyWrapAlgorithm algorithm);
        Builder context(EncryptionContext context);

        EncryptKeyRequest build();
    }
}
