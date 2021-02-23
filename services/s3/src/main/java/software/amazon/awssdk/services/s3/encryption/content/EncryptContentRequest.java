package software.amazon.awssdk.services.s3.encryption.content;

import software.amazon.awssdk.services.s3.encryption.EncryptionContext;

public interface EncryptContentRequest {
    Content plaintext();
    EncryptionContext context();

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder plaintext(Content content);
        Builder context(EncryptionContext context);
        EncryptContentRequest build();
    }
}
