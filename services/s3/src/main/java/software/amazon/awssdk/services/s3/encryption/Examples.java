package software.amazon.awssdk.services.s3.encryption;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import software.amazon.awssdk.core.sync.RequestBody;

public class Examples {
    public static void main(String... args) {
        // "Minimal" put/get

        S3EncryptedClient client =
            S3EncryptedClient.builder(EncryptionPolicy.V2020_02_16)
                             .encryptionCredentialsProvider(KmsEncryptionCredentialsProvider.create("some-id"))
                             .build();

        client.putObject(r -> r.bucket("foo").key("bar"), RequestBody.fromString("Hello, World"));

        assertThat(client.getObjectAsBytes(r -> r.bucket("foo").key("bar")).asUtf8String()).isEqualTo("Hello, World");





        // "Backwards compatible" with "old" crypto algorithms

        S3EncryptedClient client =
            S3EncryptedClient.builder(EncryptionPolicy.LEGACY)
                             .encryptionCredentialsProvider(KmsEncryptionCredentialsProvider.create("some-id"))
                             .build();

        assertThat(client.getObjectAsBytes(r -> r.bucket("foo").key("bar")).asUtf8String()).isEqualTo("Hello, World");





        // Migrate from "old" crypto algorithms to the latest recommended algorithms

        S3EncryptedClient client =
            S3EncryptedClient.builder(EncryptionPolicy.migratingBetween(EncryptionPolicy.LEGACY, EncryptionPolicy.V2020_02_16))
                             .encryptionCredentialsProvider(KmsEncryptionCredentialsProvider.create("new-id"))
                             .build();

        client.listObjectsV2Paginator(r -> r.bucket("foo"))
              .contents()
              .forEach(object -> client.reencryptObject(r -> r.bucket("foo").key(object.key())));





        // Key migration

        S3EncryptedClient client =
            S3EncryptedClient.builder(EncryptionPolicy.V2020_02_16)
                             .additionalReadCredentialsProviders(KmsEncryptionCredentialsProvider.create("old-id"))
                             .encryptionCredentialsProvider(KmsEncryptionCredentialsProvider.create("new-id"))
                             .build();

        client.listObjectsV2Paginator(r -> r.bucket("foo"))
              .contents()
              .forEach(object -> client.reencryptObject(r -> r.bucket("foo").key(object.key())));





        // Migrate unencrypted objects from the normal client to the encryption client

        EncryptionPolicy allowReadUnecryptedObjectsPolicy =
            EncryptionPolicy.V2020_02_16.copy(r -> r.addAllowedContentEncryptionAlgorithm(ContentEncryptionAlgorithm.NOT_ENCRYPTED));

        S3EncryptedClient client =
            S3EncryptedClient.builder(allowReadUnecryptedObjectsPolicy)
                             .encryptionCredentialsProvider(KmsEncryptionCredentialsProvider.create("new-id"))
                             .build();

        client.listObjectsV2Paginator(r -> r.bucket("foo"))
              .contents()
              .forEach(object -> client.reencryptObject(r -> r.bucket("foo").key(object.key())));





        // Migrate encrypted objects from the encryption client to the normal client

        EncryptionPolicy writeUnencryptedPolicy =
            EncryptionPolicy.V2020_02_16.copy(r -> r.preferredContentEncryptionAlgorithm(ContentEncryptionAlgorithm.NOT_ENCRYPTED));

        S3EncryptedClient client =
            S3EncryptedClient.builder(writeUnencryptedPolicy)
                             .encryptionCredentialsProvider(KmsEncryptionCredentialsProvider.create("new-id"))
                             .build();

        client.listObjectsV2Paginator(r -> r.bucket("foo"))
              .contents()
              .forEach(object -> client.reencryptObject(r -> r.bucket("foo").key(object.key())));




        // Transfer Manager

        TransferManager transferManager =
            TransferManager.builder()
                           .encryptionRuntime(S3EncryptionRuntime.builder(EncryptionPolicy.V2020_02_16)
                                                                 .additionalReadCredentialsProviders(KmsEncryptionCredentialsProvider.create("old-id"))
                                                                 .encryptionCredentialsProvider(KmsEncryptionCredentialsProvider.create("new-id"))
                                                                 .build())
                           .build();




        // Creating a custom credentials provider

        S3EncryptedClient client =
            S3EncryptedClient.builder(writeUnencryptedPolicy)
                             .encryptionCredentialsProvider(KmsEncryptionCredentialsProvider.create("new-id"))
                             .build();
    }
}
