package software.amazon.awsssdk.encryption.s3.internal.keywrap;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awsssdk.encryption.s3.EncryptionContext;
import software.amazon.awsssdk.encryption.s3.keywrap.DecryptKeyRequest;
import software.amazon.awsssdk.encryption.s3.keywrap.DecryptKeyResponse;
import software.amazon.awsssdk.encryption.s3.keywrap.EncryptKeyRequest;
import software.amazon.awsssdk.encryption.s3.keywrap.EncryptKeyResponse;
import software.amazon.awsssdk.encryption.s3.keywrap.KeyWrapAlgorithm;
import software.amazon.awsssdk.encryption.s3.metadata.MetadataKey;

public class AesGcmKeyEncryptor implements KeyEncryptor {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final MetadataKey<String> IV = null;
    private final SecretKey key;

    public AesGcmKeyEncryptor(SecretKey key) {
        this.key = key;
    }

    @Override
    public EncryptKeyResponse encryptKey(EncryptKeyRequest request) {
        byte[] iv = getOrCreateIv(request.context());

        try {
            Cipher cipher = Cipher.getInstance(KeyWrapAlgorithm.AES_GCM.name());
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

            byte[] encryptedKey = cipher.doFinal(request.key().getEncoded());

            return EncryptKeyResponse.builder()
                                     .key(k -> k.content(SdkBytes.fromByteArrayUnsafe(encryptedKey))
                                                .keyWrapAlgorithm(KeyWrapAlgorithm.AES_GCM)
                                                .keyAlgorithm(request.key().getAlgorithm()))
                                     .putNewMetadata(IV, Base64.getEncoder().encodeToString(iv)) // TODO: avoid decode/encode rtt?
                                     .build();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public DecryptKeyResponse decryptKey(DecryptKeyRequest request) {
        byte[] iv = getOrCreateIv(request.context());

        try {
            Cipher cipher = Cipher.getInstance(KeyWrapAlgorithm.AES_GCM.name());
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

            byte[] decryptedKey = cipher.doFinal(request.key().content().asByteArrayUnsafe());

            return DecryptKeyResponse.builder()
                                     .key(new SecretKeySpec(decryptedKey, request.key().keyAlgorithm()))
                                     .build();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private byte[] getOrCreateIv(EncryptionContext context) {
        String metadataIv = context.metadata(IV);

        if (metadataIv != null) {
            return Base64.getDecoder().decode(metadataIv);
        }

        byte[] iv = new byte[16];
        RANDOM.nextBytes(iv);
        return iv;
    }
}
