package software.amazon.awssdk.services.s3.encryption.auth;

import java.security.KeyPair;
import java.util.Optional;
import javax.crypto.SecretKey;
import software.amazon.awssdk.services.s3.encryption.keywrap.EncryptedSecretKey;
import software.amazon.awssdk.services.s3.encryption.keywrap.KeyWrapAlgorithm;

public interface EncryptionCredentials {
    Optional<EncryptedSecretKey> encryptWithAlgorithm(KeyWrapAlgorithm algorithm);

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder credentials(KeyPair credentials);
        Builder credentials(SecretKey credentials);
        EncryptionCredentials build();
    }
}
