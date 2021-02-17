package software.amazon.awssdk.services.s3.encryption;

import java.util.Map;
import javax.crypto.SecretKey;
import software.amazon.awssdk.services.s3.encryption.metadata.MetadataKey;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface EncryptionContext extends ToCopyableBuilder<EncryptionContext.Builder, EncryptionContext> {
    static Builder builder() {
        return null;
    }

    SecretKey contentEncryptionKey();
    EncryptionPolicy encryptionPolicy();
    Map<String, String> metadata();
    <T> T metadata(MetadataKey<T> key);

    interface Builder extends CopyableBuilder<EncryptionContext.Builder, EncryptionContext> {
        Builder contentEncryptionKey(SecretKey secretKey);
        Builder encryptionPolicy(EncryptionPolicy encryptionPolicy);
        Builder metadata(Map<String, String> rawMetadata);
        Builder putMetadata(String key, String value);
        <T> Builder putMetadata(MetadataKey<T> key, T value);

        EncryptionContext build();
    }
}
