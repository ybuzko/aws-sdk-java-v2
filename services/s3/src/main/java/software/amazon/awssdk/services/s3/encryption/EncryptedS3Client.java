package software.amazon.awssdk.services.s3.encryption;

import software.amazon.awssdk.services.s3.S3Client;

public interface EncryptedS3Client extends S3Client {
    static Builder builder(EncryptionProfile encryptionProfile) {
        return null;
    }

//    interface Builder {
//        Builder addAdditionalReadCredentialsProvider(EncryptionCredentialsProvider credentialsProvider);
//        Builder readWriteCredentialsProvider(EncryptionCredentialsProvider credentialsProvider);
//        EncryptedS3Client build();
//    }

    interface Builder {
        Builder addAdditionalReadCredentialsProvider(EncryptionCredentialsProvider credentialsProvider);
        Builder readWriteCredentialsProvider(EncryptionCredentialsProvider credentialsProvider);
        EncryptedS3Client build();
    }
}
