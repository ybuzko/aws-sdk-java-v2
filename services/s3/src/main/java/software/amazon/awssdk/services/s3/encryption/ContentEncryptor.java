package software.amazon.awssdk.services.s3.encryption;

public interface ContentEncryptor {
    ContentEncryptor AES_GCM = null;

    ContentEncryptor AES_CBC = null;

    @Deprecated
    ContentEncryptor AES_CTR = null;

    String name();

    Content encryptContent(Content requestBody, EncryptionContext context);
    AsyncContent encryptContent(AsyncContent requestBody, EncryptionContext context);

    Content decryptContent(Content requestBody, EncryptionContext context);
    AsyncContent decryptContent(AsyncContent requestBody, EncryptionContext context);
}
