package software.amazon.awsssdk.encryption.s3.internal;

import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;

@FunctionalInterface
public interface KeyGeneratorProvider {
    KeyGeneratorProvider AES_256 = () -> {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return keyGenerator;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    };

    KeyGenerator createKeyGenerator();
}
