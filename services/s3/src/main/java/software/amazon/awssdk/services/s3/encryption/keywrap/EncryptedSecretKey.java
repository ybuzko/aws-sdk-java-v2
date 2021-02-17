package software.amazon.awssdk.services.s3.encryption.keywrap;

import software.amazon.awssdk.core.SdkBytes;

public interface EncryptedSecretKey {
    SdkBytes content();
    String keyAlgorithm();
    KeyWrapAlgorithm keyWrapAlgorithm();

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder content(SdkBytes content);
        Builder keyAlgorithm(String keyAlgorithm);
        Builder keyWrapAlgorithm(KeyWrapAlgorithm algorithm);

        EncryptedSecretKey build();
    }
}
