package software.amazon.awssdk.services.s3.encryption.keywrap;

import java.util.Map;
import java.util.function.Consumer;
import software.amazon.awssdk.services.s3.encryption.EncryptionContext;
import software.amazon.awssdk.services.s3.encryption.auth.ResolveKeyResponse;
import software.amazon.awssdk.services.s3.encryption.metadata.MetadataKey;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface EncryptKeyResponse extends ToCopyableBuilder<EncryptKeyResponse.Builder, EncryptKeyResponse> {
    EncryptedSecretKey key();
    Map<String, String> newMetadata();

    static Builder builder() {
        return null;
    }

    interface Builder extends CopyableBuilder<Builder, EncryptKeyResponse> {
        Builder key(EncryptedSecretKey key);
        Builder key(Consumer<EncryptedSecretKey.Builder> key);

        <T> Builder putNewMetadata(MetadataKey<T> key, T value);

        EncryptKeyResponse build();
    }
}
