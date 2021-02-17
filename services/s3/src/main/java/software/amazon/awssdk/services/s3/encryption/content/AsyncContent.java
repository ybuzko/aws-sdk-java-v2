package software.amazon.awssdk.services.s3.encryption.content;

import org.reactivestreams.Publisher;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.s3.encryption.content.Content;

public interface AsyncContent {
    Publisher<SdkBytes> inputStream();
    long contentLength();

    static Content create(Publisher<SdkBytes> inputStream, long length) {
        return null;
    }
}
