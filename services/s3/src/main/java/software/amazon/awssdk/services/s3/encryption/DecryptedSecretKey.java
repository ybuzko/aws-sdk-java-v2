package software.amazon.awssdk.services.s3.encryption;

import javax.crypto.SecretKey;

public interface DecryptedSecretKey {
    SecretKey key();

    static DecryptedSecretKey create(SecretKey key) {
        return null;
    }
}
