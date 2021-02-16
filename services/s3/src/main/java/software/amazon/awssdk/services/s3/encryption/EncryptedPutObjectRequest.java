package software.amazon.awssdk.services.s3.encryption;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public interface EncryptedPutObjectRequest {
    PutObjectRequest request();
    RequestBody body();
}
