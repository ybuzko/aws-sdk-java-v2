package software.amazon.awsssdk.encryption.s3.content;

import software.amazon.awsssdk.encryption.s3.EncryptionContext;

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
