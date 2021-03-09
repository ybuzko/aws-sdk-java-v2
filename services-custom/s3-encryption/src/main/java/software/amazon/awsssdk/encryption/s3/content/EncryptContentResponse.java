package software.amazon.awsssdk.encryption.s3.content;

public interface EncryptContentResponse {
    Content ciphertext();

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder ciphertext(Content content);
        EncryptContentResponse build();
    }
}
