package software.amazon.awssdk.services.s3.encryption;

public interface ContentKeyEncryptionAlgorithm {
    ContentKeyEncryptionAlgorithm RSA_OAEP_SHA1 = null; // "RSA-OAEP-SHA1"

    ContentKeyEncryptionAlgorithm AES_GCM = null; // "AES/GCM"

    ContentKeyEncryptionAlgorithm KMS = null; // "kms+context"

    @Deprecated
    ContentKeyEncryptionAlgorithm KMS_NO_CONTEXT = null; // "kms"

    @Deprecated
    ContentKeyEncryptionAlgorithm AES = null; // "AESWrap"

    @Deprecated
    ContentKeyEncryptionAlgorithm RSA_ECB_OAEP_SHA256_MGF1_PADDING = null; // "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"

    String name();

    ContentKeyEncryptor createEncryptor(EncryptionCredentials credentials);

    boolean supports(EncryptionCredentials credentials);
}
