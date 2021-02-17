package software.amazon.awssdk.services.s3.encryption.model;

import java.util.Map;
import software.amazon.awssdk.services.s3.encryption.Content;

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
