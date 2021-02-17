package software.amazon.awssdk.services.s3.encryption.model;

import software.amazon.awssdk.services.s3.encryption.ContentKeyEncryptionAlgorithm;
import software.amazon.awssdk.services.s3.encryption.EncryptionContext;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface ResolveKeyRequest extends ToCopyableBuilder<Builder, ResolveKeyRequest> {
    EncryptionContext context();
    ContentKeyEncryptionAlgorithm algorithm();

    static Builder builder() {
        return null;
    }

    interface Builder extends CopyableBuilder<Builder, ResolveKeyRequest> {
        Builder context(EncryptionContext context);
        Builder algorithm(ContentKeyEncryptionAlgorithm algorithm);
        ResolveKeyRequest build();
    }
}
