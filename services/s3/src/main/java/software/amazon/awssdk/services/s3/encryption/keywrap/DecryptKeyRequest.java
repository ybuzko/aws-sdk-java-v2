package software.amazon.awssdk.services.s3.encryption.keywrap;

import software.amazon.awssdk.services.s3.encryption.EncryptionContext;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface DecryptKeyRequest extends ToCopyableBuilder<DecryptKeyRequest.Builder, DecryptKeyRequest> {
    EncryptedSecretKey key();
    EncryptionContext context();

    static Builder builder() {
        return null;
    }

    interface Builder extends CopyableBuilder<Builder, DecryptKeyRequest> {
        Builder key(EncryptedSecretKey key);
        Builder context(EncryptionContext context);

        DecryptKeyRequest build();
    }
}
