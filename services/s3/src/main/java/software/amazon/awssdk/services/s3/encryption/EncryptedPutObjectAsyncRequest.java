package software.amazon.awssdk.services.s3.encryption;

import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public interface EncryptedPutObjectAsyncRequest {
    PutObjectRequest request();
    AsyncRequestBody body();
}
