package software.amazon.awsssdk.encryption.s3.content;

import java.io.InputStream;

public interface Content {
    InputStream inputStream();
    long contentLength();

    static Content create(InputStream inputStream, long length) {
        return null;
    }
}
