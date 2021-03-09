package software.amazon.awsssdk.encryption.s3.keywrap;

import software.amazon.awsssdk.encryption.s3.EncryptionContext;
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
