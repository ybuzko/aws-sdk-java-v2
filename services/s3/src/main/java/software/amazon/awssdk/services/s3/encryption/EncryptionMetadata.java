package software.amazon.awssdk.services.s3.encryption;

import java.util.Map;

public interface EncryptionMetadata {
    static EncryptionMetadata empty() {
        return null;
    }

    Map<String, String> rawMetadata();
}
