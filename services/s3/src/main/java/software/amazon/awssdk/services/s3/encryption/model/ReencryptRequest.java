package software.amazon.awssdk.services.s3.encryption.model;

public interface ReencryptRequest {
    static Builder builder() {
        return null;
    }

    String bucket();
    String key();

    interface Builder {
        Builder bucket(String bucket);
        Builder key(String key);
        ReencryptRequest build();
    }
}

