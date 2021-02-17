package software.amazon.awssdk.services.s3.encryption.content;

import java.util.Map;

public interface EncryptContentRequest {
    Content content();
    Map<String, String> metadata();
    String contentType();
    String contentEncoding();

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder content(Content content);
        Builder metadata(Map<String, String> metadata);
        Builder contentType(String contentType);
        Builder contentEncoding(String contentEncoding);
        EncryptContentRequest build();
    }
}
