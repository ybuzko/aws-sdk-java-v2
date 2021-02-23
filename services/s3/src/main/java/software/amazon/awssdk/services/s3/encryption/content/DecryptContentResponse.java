package software.amazon.awssdk.services.s3.encryption.content;

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
