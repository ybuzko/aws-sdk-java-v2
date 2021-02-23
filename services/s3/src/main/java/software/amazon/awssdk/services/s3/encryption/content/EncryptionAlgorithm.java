package software.amazon.awssdk.services.s3.encryption.content;

public interface EncryptionAlgorithm {
    EncryptionAlgorithm AES_GCM = null; // "AES/GCM/NoPadding"
    EncryptionAlgorithm AES_CBC = null; // "AES/CBC/PKCS5Padding"

    @Deprecated
    EncryptionAlgorithm AES_CTR = null; // "AES/CTR/NoPadding"

    @Deprecated
    EncryptionAlgorithm NOT_ENCRYPTED = null;

    ContentEncryptor createContentEncryptor();
}
