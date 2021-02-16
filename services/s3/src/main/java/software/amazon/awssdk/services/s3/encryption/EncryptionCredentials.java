package software.amazon.awssdk.services.s3.encryption;

import java.security.KeyPair;
import java.util.Map;
import javax.crypto.SecretKey;
import software.amazon.awssdk.utils.Either;

public interface EncryptionCredentials {
    KeyWrapAlgorithm wrapAlgorithm();
    Either<KeyPair, SecretKey> credentials();
}
