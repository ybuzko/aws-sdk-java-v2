package software.amazon.awssdk.services.s3.encryption;

import java.security.KeyPair;
import java.util.Optional;
import javax.crypto.SecretKey;

public interface EncryptionCredentials {
    Optional<EncryptedSecretKey> encryptWithAlgorithm(ContentKeyEncryptionAlgorithm algorithm);

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder credentials(KeyPair credentials);
        Builder credentials(SecretKey credentials);
        EncryptionCredentials build();
    }
}
