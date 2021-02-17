package software.amazon.awssdk.services.s3.encryption;

import java.util.Map;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface EncryptedObjectMetadata extends ToCopyableBuilder<EncryptedObjectMetadata.Builder, EncryptedObjectMetadata> {
    EncryptedSecretKey encryptedSecretKey();
    Map<String, String> toRawMetadata();

    static EncryptedObjectMetadata create(Map<String, String> rawMetadata) {
        return null;
    }

    interface Builder extends CopyableBuilder<Builder, EncryptedObjectMetadata> {
        Builder encryptedSecretKey(EncryptedSecretKey encryptedSecretKey);
    }
}
