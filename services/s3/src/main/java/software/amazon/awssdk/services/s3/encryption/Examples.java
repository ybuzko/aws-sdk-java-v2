package software.amazon.awssdk.services.s3.encryption;

import java.nio.file.Path;
import java.nio.file.Paths;
import software.amazon.awssdk.core.sync.RequestBody;

public class Examples {
    public static void main(String... args) {
        EncryptedS3Client client =
            EncryptedS3Client.builder(EncryptionProfile.LEGACY)
                             .encryptionCredentialsProvider(KmsEncryptionCredentialsProvider.create("some-id"))
                             .build();

        client.putObject(r -> r.bucket("foo").key("bar"), RequestBody.fromString("Hello, World."));
    }
}
