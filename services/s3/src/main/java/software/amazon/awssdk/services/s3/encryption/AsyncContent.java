package software.amazon.awssdk.services.s3.encryption;

import org.reactivestreams.Publisher;
import software.amazon.awssdk.core.SdkBytes;

public interface AsyncContent {
    Publisher<SdkBytes> inputStream();
    long contentLength();

    static Content create(Publisher<SdkBytes> inputStream, long length) {
        return null;
    }
}
