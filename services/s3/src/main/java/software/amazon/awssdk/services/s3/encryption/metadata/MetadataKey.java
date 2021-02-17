package software.amazon.awssdk.services.s3.encryption.metadata;

import java.util.Map;
import software.amazon.awssdk.services.s3.encryption.keywrap.EncryptedSecretKey;

public interface MetadataKey<T> {
    MetadataKey<String> KEY_WRAP_ALGORITHM = null;
    MetadataKey<EncryptedSecretKey> ENCRYPTED_SECRET_KEY = null; // TODO
    MetadataKey<String> KEY_ALGORITHM = null; // TODO

    T read(Map<String, String> metadata);
}
