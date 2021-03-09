package software.amazon.awsssdk.encryption.s3.auth;

import java.util.HashSet;
import java.util.Set;
import javax.crypto.SecretKey;
import software.amazon.awsssdk.encryption.s3.internal.keywrap.AesGcmKeyEncryptor;
import software.amazon.awsssdk.encryption.s3.internal.keywrap.KeyEncryptor;
import software.amazon.awsssdk.encryption.s3.keywrap.DecryptKeyRequest;
import software.amazon.awsssdk.encryption.s3.keywrap.DecryptKeyResponse;
import software.amazon.awsssdk.encryption.s3.keywrap.EncryptKeyRequest;
import software.amazon.awsssdk.encryption.s3.keywrap.EncryptKeyResponse;
import software.amazon.awsssdk.encryption.s3.keywrap.KeyWrapAlgorithm;

public class AsymmetricEncryptionCredentials implements EncryptionCredentials {
    private final String id;
    private final SecretKey secretKey;

    private AsymmetricEncryptionCredentials(String id, SecretKey secretKey) {
        this.id = id;
        this.secretKey = secretKey;
    }

    public static AsymmetricEncryptionCredentials create(String id, SecretKey secretKey) {
        return new AsymmetricEncryptionCredentials(id, secretKey);
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
