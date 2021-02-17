package software.amazon.awssdk.services.s3.encryption.content;

import java.util.Map;

public interface DecryptContentRequest {
    Content content();
    Map<String, String> metadata();

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder content(Content content);
        Builder metadata(Map<String, String> metadata);
        DecryptContentRequest build();
    }
}
