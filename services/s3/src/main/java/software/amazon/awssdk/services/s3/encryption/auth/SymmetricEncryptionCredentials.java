package software.amazon.awssdk.services.s3.encryption.auth;

import java.util.HashSet;
import java.util.Set;
import javax.crypto.SecretKey;
import software.amazon.awssdk.services.s3.encryption.internal.keywrap.AesGcmKeyEncryptor;
import software.amazon.awssdk.services.s3.encryption.internal.keywrap.KeyEncryptor;
import software.amazon.awssdk.services.s3.encryption.keywrap.DecryptKeyRequest;
import software.amazon.awssdk.services.s3.encryption.keywrap.DecryptKeyResponse;
import software.amazon.awssdk.services.s3.encryption.keywrap.EncryptKeyRequest;
import software.amazon.awssdk.services.s3.encryption.keywrap.EncryptKeyResponse;
import software.amazon.awssdk.services.s3.encryption.keywrap.KeyWrapAlgorithm;

public class SymmetricEncryptionCredentials implements EncryptionCredentials {
    private final String id;
    private final SecretKey secretKey;

    private SymmetricEncryptionCredentials(String id, SecretKey secretKey) {
        this.id = id;
        this.secretKey = secretKey;
    }

    public static SymmetricEncryptionCredentials create(String id, SecretKey secretKey) {
        return new SymmetricEncryptionCredentials(id, secretKey);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Set<KeyWrapAlgorithm> supportedKeyWrapAlgorithms() {
        Set<KeyWrapAlgorithm> result = new HashSet<>();
        result.add(KeyWrapAlgorithm.AES_GCM);
//        result.add(KeyWrapAlgorithm.AES);
        return result;
    }

    @Override
    public EncryptKeyResponse encryptKey(EncryptKeyRequest request) {
        return encryptor(request.keyWrapAlgorithm()).encryptKey(request);
    }

    @Override
    public DecryptKeyResponse decryptKey(DecryptKeyRequest request) {
        return encryptor(request.key().keyWrapAlgorithm()).decryptKey(request);
    }

    private KeyEncryptor encryptor(KeyWrapAlgorithm algorithm) {
        if (algorithm == KeyWrapAlgorithm.AES_GCM) {
            return new AesGcmKeyEncryptor(secretKey);
        } else {
            throw new IllegalArgumentException("Unsupported key-wrap algorithm: " + algorithm);
        }
    }
}
