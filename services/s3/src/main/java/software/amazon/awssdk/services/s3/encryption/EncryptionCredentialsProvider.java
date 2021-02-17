package software.amazon.awssdk.services.s3.encryption;

import java.util.Set;
import java.util.function.Consumer;
import software.amazon.awssdk.services.s3.encryption.model.ResolveKeyRequest;
import software.amazon.awssdk.services.s3.encryption.model.ResolveKeyResponse;

public interface EncryptionCredentialsProvider {
    Set<ContentKeyEncryptionAlgorithm> supportedContentEncryptionKeyAlgorithms();

    ResolveKeyResponse resolveContentKey(ResolveKeyRequest request);
    default ResolveKeyResponse resolveContentKey(Consumer<ResolveKeyRequest.Builder> request) {
        ResolveKeyRequest.Builder builder = ResolveKeyRequest.builder();
        request.accept(builder);
        return resolveContentKey(builder.build());
    }
}
