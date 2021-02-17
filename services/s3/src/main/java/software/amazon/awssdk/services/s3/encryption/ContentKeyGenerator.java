package software.amazon.awssdk.services.s3.encryption;

import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

@FunctionalInterface
public interface ContentKeyGenerator {
    ContentKeyGenerator AES_256 = () -> {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return DecryptedSecretKey.create(keyGenerator.generateKey());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    };

    DecryptedSecretKey generateKey();
}
