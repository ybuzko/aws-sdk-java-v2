package software.amazon.awssdk.services.s3.encryption;

import java.io.InputStream;

public interface Content {
    InputStream inputStream();
    long contentLength();

    static Content create(InputStream inputStream, long length) {
        return null;
    }
}
