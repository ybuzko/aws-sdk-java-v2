package software.amazon.awssdk.services.s3.encryption.content;

import software.amazon.awssdk.services.s3.encryption.EncryptionContext;

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
