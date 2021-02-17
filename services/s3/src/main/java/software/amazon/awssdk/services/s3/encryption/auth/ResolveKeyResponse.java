package software.amazon.awssdk.services.s3.encryption.auth;

import java.util.Map;
import javax.crypto.SecretKey;
import software.amazon.awssdk.services.s3.encryption.metadata.MetadataKey;

public interface ResolveKeyResponse {
    SecretKey secretKey();
    Map<String, String> newMetadata();

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder secretKey(SecretKey secretKey);
        <T> Builder putNewMetadata(MetadataKey<T> key, T value);

        ResolveKeyResponse build();
    }
}
