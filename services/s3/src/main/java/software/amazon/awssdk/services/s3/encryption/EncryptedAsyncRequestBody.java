package software.amazon.awssdk.services.s3.encryption;

import software.amazon.awssdk.core.async.AsyncRequestBody;

public interface EncryptedAsyncRequestBody extends EncryptedContent {
    AsyncRequestBody requestBody();
}
