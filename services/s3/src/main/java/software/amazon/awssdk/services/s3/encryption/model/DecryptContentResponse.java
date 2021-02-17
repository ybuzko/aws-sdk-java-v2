package software.amazon.awssdk.services.s3.encryption.model;

import software.amazon.awssdk.services.s3.encryption.Content;

public interface DecryptContentResponse {
    Content content();
    String contentType();
    String contentEncoding();

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder content(Content content);
        Builder contentType(String contentType);
        Builder contentEncoding(String contentEncoding);
        DecryptContentResponse build();
    }
}
