package software.amazon.awsssdk.encryption.s3.content;

public interface DecryptObjectResponse {
    Content plaintext();
    String contentType();
    String contentEncoding();

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder plaintext(Content content);
        Builder contentType(String contentType);
        Builder contentEncoding(String contentEncoding);
        DecryptObjectResponse build();
    }
}
