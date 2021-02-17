package software.amazon.awssdk.services.s3.encryption.model;

import java.util.Map;
import software.amazon.awssdk.services.s3.encryption.DecryptedSecretKey;
import software.amazon.awssdk.services.s3.encryption.metadata.MetadataKey;

public interface ResolveKeyResponse {
    DecryptedSecretKey secretKey();
    Map<String, String> newMetadata();

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder secretKey(DecryptedSecretKey secretKey);
        <T> Builder putNewMetadata(MetadataKey<T> key, T value);

        ResolveKeyResponse build();
    }
}
