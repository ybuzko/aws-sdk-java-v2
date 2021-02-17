package software.amazon.awssdk.services.s3.encryption;

public interface ContentKeyEncryptor {
    String name();
    
    EncryptedSecretKey encryptKey(DecryptedSecretKey key, EncryptionContext context);
    DecryptedSecretKey decryptKey(EncryptedSecretKey key, EncryptionContext context);

    ContentKeyEncryptor RSA_OAEP_SHA1 = null;

    ContentKeyEncryptor AES_GCM = null;

    ContentKeyEncryptor KMS = null;

    @Deprecated
    ContentKeyEncryptor KMS_NO_CONTEXT = null;

    @Deprecated
    ContentKeyEncryptor AES = null;

    @Deprecated
    ContentKeyEncryptor RSA_ECB_OAEP_SHA256_MGF1_PADDING = null;
}
