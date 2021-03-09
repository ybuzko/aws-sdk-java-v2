package software.amazon.awsssdk.encryption.s3.content;

import java.util.Map;

public interface DecryptObjectRequest {
    Content ciphertext();
    Map<String, String> metadata();

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder ciphertext(Content content);
        Builder metadata(Map<String, String> metadata);
        DecryptObjectRequest build();
    }
}
