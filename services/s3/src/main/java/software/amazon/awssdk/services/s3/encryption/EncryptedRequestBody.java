package software.amazon.awssdk.services.s3.encryption;

import software.amazon.awssdk.core.sync.RequestBody;

public interface EncryptedRequestBody extends EncryptedContent {
    RequestBody requestBody();
}
