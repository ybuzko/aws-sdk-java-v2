package software.amazon.awssdk.services.s3.encryption;

import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.sync.RequestBody;

public interface ContentEncryptor {
    RequestBody encryptContent(RequestBody requestBody, EncryptionContext context);
    AsyncRequestBody encryptContent(AsyncRequestBody requestBody, EncryptionContext context);

    RequestBody decryptContent(RequestBody requestBody, EncryptionContext context);
    AsyncRequestBody decryptContent(AsyncRequestBody requestBody, EncryptionContext context);

    public Builder builder() {
        return null;
    }

    public static final ContentEncryptor AES_GCM = null;
    public static final ContentEncryptor AES_CBC = null;

    @Deprecated
    public static final ContentEncryptor AES_CTR = null;

    interface Builder {
        Builder putReadEncryptorMapping(ContentEncryptionAlgorithm algorithm, ContentEncryptor encryptor);
        Builder writeEncryptor(ContentEncryptor encryptor);

        ContentEncryptor build();
    }
}
