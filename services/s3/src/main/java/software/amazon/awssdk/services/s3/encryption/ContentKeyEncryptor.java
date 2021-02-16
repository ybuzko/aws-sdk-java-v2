package software.amazon.awssdk.services.s3.encryption;

import java.util.Optional;
import javax.crypto.SecretKey;

public interface ContentKeyEncryptor {
    Optional<String> name();
    EncryptedSecretKey encryptKey(SecretKey key, EncryptionContext context);
    SecretKey decryptKey(EncryptedSecretKey key, EncryptionContext context);

    public Builder builder() {
        return null;
    }

    interface Builder {
        Builder putReadEncryptorMapping(KeyWrapAlgorithm algorithm, ContentKeyEncryptor encryptor);
        Builder writeEncryptor(ContentKeyEncryptor encryptor);

        ContentKeyEncryptor build();
    }
}
