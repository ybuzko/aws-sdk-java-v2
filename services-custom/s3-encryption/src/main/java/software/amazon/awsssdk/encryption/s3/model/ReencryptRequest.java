package software.amazon.awsssdk.encryption.s3.model;

public interface ReencryptRequest {
    String bucket();
    String key();

    static Builder builder() {
        return null;
    }

    interface Builder {
        Builder bucket(String bucket);
        Builder key(String key);
        ReencryptRequest build();
    }
}

