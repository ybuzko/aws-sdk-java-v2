package software.amazon.awsssdk.encryption.s3.auth;

import software.amazon.awsssdk.encryption.s3.keywrap.KeyWrapAlgorithm;
import software.amazon.awsssdk.encryption.s3.EncryptionContext;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface ResolveKeyRequest extends ToCopyableBuilder<ResolveKeyRequest.Builder, ResolveKeyRequest> {
    EncryptionContext context();
    KeyWrapAlgorithm keyWrapAlgorithm();
    EncryptionCredentials credentialsProvider();

    static Builder builder() {
        return null;
    }

    interface Builder extends CopyableBuilder<Builder, ResolveKeyRequest> {
        Builder context(EncryptionContext context);
        Builder algorithm(KeyWrapAlgorithm algorithm);
        Builder credentialsProvider(EncryptionCredentials credentialsProvider);
        ResolveKeyRequest build();
    }
}
