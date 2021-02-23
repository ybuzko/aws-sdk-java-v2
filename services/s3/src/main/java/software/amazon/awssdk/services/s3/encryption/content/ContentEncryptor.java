package software.amazon.awssdk.services.s3.encryption.content;

import java.util.function.Consumer;

public interface ContentEncryptor {
    ContentEncryptor AES_GCM = null;

    ContentEncryptor AES_CBC = null;

    @Deprecated
    ContentEncryptor AES_CTR = null;

    String name();

    EncryptContentResponse encryptContent(EncryptContentRequest request);

    EncryptContentResponse encryptContent(Consumer<EncryptContentRequest.Builder> request);

    DecryptContentResponse decryptContent(DecryptContentRequest request);

    DecryptContentResponse decryptContent(Consumer<DecryptContentRequest.Builder> request);
}
