package software.amazon.awssdk.services.s3.encryption.auth;

import java.util.Set;
import java.util.function.Consumer;
import software.amazon.awssdk.services.s3.encryption.keywrap.KeyEncryptor;
import software.amazon.awssdk.services.s3.encryption.keywrap.KeyWrapAlgorithm;
import software.amazon.awssdk.services.s3.encryption.model.CreateKeyRequest;

public interface EncryptionCredentialsProvider {
    Set<KeyWrapAlgorithm> supportedKeyWrapAlgorithms();

    KeyEncryptor createKeyEncryptor(KeyWrapAlgorithm algorithm);

//    CreateKeyResponse createKey(CreateKeyRequest request);



//    ResolveKeyResponse resolveKey(ResolveKeyRequest request);

//    default ResolveKeyResponse resolveKey(Consumer<ResolveKeyRequest.Builder> request) {
//        ResolveKeyRequest.Builder builder = ResolveKeyRequest.builder();
//        request.accept(builder);
//        return resolveKey(builder.build());
//    }
}
