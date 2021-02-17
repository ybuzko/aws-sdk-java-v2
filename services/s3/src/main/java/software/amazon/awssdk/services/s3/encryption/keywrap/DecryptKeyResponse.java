package software.amazon.awssdk.services.s3.encryption.keywrap;

import javax.crypto.SecretKey;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface DecryptKeyResponse extends ToCopyableBuilder<DecryptKeyResponse.Builder, DecryptKeyResponse> {
    SecretKey key();

    static Builder builder() {
        return null;
    }

    interface Builder extends CopyableBuilder<Builder, DecryptKeyResponse> {
        Builder key(SecretKey key);

        DecryptKeyResponse build();
    }
}
