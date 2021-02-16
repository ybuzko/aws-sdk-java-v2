package software.amazon.awssdk.services.s3.encryption;

public interface EncryptionCredentialsProvider {
    EncryptionCredentials resolveCredentials();
}
