package software.amazon.awsssdk.encryption.s3.content;

import java.util.Map;

public interface EncryptObjectRequest {
    Content plaintext();
    Map<String, String> metadata();
    String contentType();
    String contentEncoding();

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder plaintext(Content content);
        Builder metadata(Map<String, String> metadata);
        Builder contentType(String contentType);
        Builder contentEncoding(String contentEncoding);
        EncryptObjectRequest build();
    }
}
