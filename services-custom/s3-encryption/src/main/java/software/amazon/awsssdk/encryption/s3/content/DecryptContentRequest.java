package software.amazon.awsssdk.encryption.s3.content;

import software.amazon.awsssdk.encryption.s3.EncryptionContext;

public interface DecryptContentRequest {
    Content ciphertext();
    EncryptionContext context();

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder ciphertext(Content content);
        Builder context(EncryptionContext context);
        DecryptContentRequest build();
    }
}
