package software.amazon.awssdk.services.s3.encryption.auth;

import software.amazon.awssdk.services.s3.encryption.keywrap.KeyWrapAlgorithm;
import software.amazon.awssdk.services.s3.encryption.EncryptionContext;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface ResolveKeyRequest extends ToCopyableBuilder<ResolveKeyRequest.Builder, ResolveKeyRequest> {
    EncryptionContext context();
    KeyWrapAlgorithm algorithm();
    EncryptionCredentialsProvider credentialsProvider();

    static Builder builder() {
        return null;
    }

    interface Builder extends CopyableBuilder<Builder, ResolveKeyRequest> {
        Builder context(EncryptionContext context);
        Builder algorithm(KeyWrapAlgorithm algorithm);
        Builder credentialsProvider(EncryptionCredentialsProvider credentialsProvider);
        ResolveKeyRequest build();
    }
}
