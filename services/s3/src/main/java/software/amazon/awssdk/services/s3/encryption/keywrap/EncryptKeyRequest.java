package software.amazon.awssdk.services.s3.encryption.keywrap;

import javax.crypto.SecretKey;
import software.amazon.awssdk.services.s3.encryption.EncryptionContext;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface EncryptKeyRequest extends ToCopyableBuilder<EncryptKeyRequest.Builder, EncryptKeyRequest> {
    SecretKey key();
    EncryptionContext context();

    static Builder builder() {
        return null;
    }

    interface Builder extends CopyableBuilder<Builder, EncryptKeyRequest> {
        Builder key(SecretKey key);
        Builder context(EncryptionContext context);

        EncryptKeyRequest build();
    }
}
