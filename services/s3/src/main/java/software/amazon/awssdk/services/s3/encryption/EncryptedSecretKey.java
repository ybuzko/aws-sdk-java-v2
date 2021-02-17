package software.amazon.awssdk.services.s3.encryption;

public interface EncryptedSecretKey {
    byte[] content();
    ContentKeyEncryptionAlgorithm algorithm();

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder algorithm(ContentKeyEncryptionAlgorithm algorithm);
        Builder content(byte[] content);

        EncryptedSecretKey build();
    }
}
