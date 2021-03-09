package software.amazon.awsssdk.encryption.s3.content;

public interface DecryptContentResponse {
    Content plaintext();

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder plaintext(Content content);
        DecryptContentResponse build();
    }
}
