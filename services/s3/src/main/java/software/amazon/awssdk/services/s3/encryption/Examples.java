package software.amazon.awssdk.services.s3.encryption;

import static software.amazon.awssdk.services.s3.encryption.content.EncryptionAlgorithm.NOT_ENCRYPTED;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.encryption.auth.EncryptionCredentials;
import software.amazon.awssdk.services.s3.encryption.auth.KmsEncryptionCredentials;
import software.amazon.awssdk.services.s3.encryption.auth.SymmetricEncryptionCredentials;
import software.amazon.awssdk.services.s3.encryption.content.EncryptionAlgorithm;
import software.amazon.awssdk.services.s3.encryption.internal.KeyGeneratorProvider;
import software.amazon.awssdk.services.s3.encryption.keywrap.KeyWrapAlgorithm;

public class Examples {
    public static void main(String... args) {
        SecretKey oldKey = new SecretKeySpec(...);
        SecretKey newKey = new SecretKeySpec(...);
        SecretKey secretKey = new SecretKeySpec(...);

        // "Minimal" put/get

        EncryptionCredentials credentials = SymmetricEncryptionCredentials.create(secretKey);

        S3EncryptionClient client = S3EncryptionClient.builder(EncryptionPolicy.V2020_02_16)
                                                      .encryptionCredentials(credentials)
                                                      .build();

        client.putObject(r -> r.bucket("foo").key("bar"),
                         RequestBody.fromString("Hello, World"));

        client.getObject(r -> r.bucket("foo").key("bar"));





        // "Backwards compatible" with "old" crypto algorithms

S3EncryptionClient client = S3EncryptionClient.builder(EncryptionPolicy.LEGACY)
                                              .encryptionCredentials(credentials)
                                              .build();

client.getObject(r -> r.bucket("foo").key("bar"));





        // Migrate from "old" crypto algorithms to the latest recommended algorithms

        EncryptionPolicy encryptionPolicy =
            EncryptionPolicy.migratingBetween(EncryptionPolicy.LEGACY,
                                              EncryptionPolicy.V2020_02_16);

        S3EncryptionClient client = S3EncryptionClient.builder(encryptionPolicy)
                                                      .encryptionCredentials(credentials)
                                                      .build();

        client.listObjectsV2Paginator(r -> r.bucket("foo"))
              .contents()
              .forEach(object -> client.reencryptObject(r -> r.bucket("foo")
                                                              .key(object.key())));





        // Key migration

EncryptionCredentials oldCreds = SymmetricEncryptionCredentials.create(oldKey);
EncryptionCredentials newCreds = SymmetricEncryptionCredentials.create(newKey);

S3EncryptionClient client =
    S3EncryptionClient.builder(EncryptionPolicy.V2020_02_16)
                      .readEncryptionCredentials(oldCreds)
                      .encryptionCredentials(newCreds)
                      .build();

client.listObjectsV2Paginator(r -> r.bucket("foo"))
      .contents()
      .forEach(object -> client.reencryptObject(r -> r.bucket("foo")
                                                      .key(object.key())));





        // Migrate unencrypted objects from the normal client to the encryption client

EncryptionPolicy allowReadUnecryptedObjectsPolicy =
    EncryptionPolicy.V2020_02_16
                    .toBuilder()
                    .addAllowedContentEncryptionAlgorithm(NOT_ENCRYPTED)
                    .build();

S3EncryptionClient client =
    S3EncryptionClient.builder(allowReadUnecryptedObjectsPolicy)
                      .encryptionCredentials(credentials)
                      .build();

client.listObjectsV2Paginator(r -> r.bucket("foo"))
      .contents()
      .forEach(object -> client.reencryptObject(r -> r.bucket("foo")
                                                      .key(object.key())));





        // Migrate encrypted objects from the encryption client to the normal client

EncryptionPolicy writeUnencryptedPolicy =
    EncryptionPolicy.V2020_02_16
        .toBuilder()
        .preferredContentEncryptionAlgorithm(NOT_ENCRYPTED)
        .build();

S3EncryptionClient client =
    S3EncryptionClient.builder(writeUnencryptedPolicy)
                      .encryptionCredentials(credentials)
                      .build();

client.listObjectsV2Paginator(r -> r.bucket("foo"))
      .contents()
      .forEach(object -> client.reencryptObject(r -> r.bucket("foo")
                                                      .key(object.key())));

        EncryptionPolicy writeUnencryptedPolicy =
            EncryptionPolicy.V2020_02_16.copy(r -> r.preferredContentEncryptionAlgorithm(NOT_ENCRYPTED));

        S3EncryptionClient client =
            S3EncryptionClient.builder(writeUnencryptedPolicy)
                              .encryptionCredentials(SymmetricEncryptionCredentials.create(secretKey))
                              .build();

        client.listObjectsV2Paginator(r -> r.bucket("foo"))
              .contents()
              .forEach(object -> client.reencryptObject(r -> r.bucket("foo").key(object.key())));





        // Transfer Manager

S3EncryptionRuntime encryptionRuntime =
    S3EncryptionRuntime.builder(EncryptionPolicy.V2020_02_16)
                       .encryptionCredentials(SymmetricEncryptionCredentials.create(secretKey))
                       .build();

TransferManager transferManager =
    TransferManager.builder()
                   .encryptionRuntime(encryptionRuntime)
                   .build();





        // Creating a custom encryption policy

EncryptionPolicy customPolicy =
    EncryptionPolicy.builder()
                    .allowedKeyEncryptionAlgorithms(KeyWrapAlgorithm.KMS)
                    .preferredKeyEncryptionAlgorithms(KeyWrapAlgorithm.KMS)
                    .allowedContentEncryptionAlgorithms(EncryptionAlgorithm.AES_GCM)
                    .preferredContentEncryptionAlgorithm(EncryptionAlgorithm.AES_GCM)
                    .contentKeyGenerator(KeyGeneratorProvider.AES_256)
                    .build();

S3EncryptionClient client =
    S3EncryptionClient.builder(customPolicy)
                      .encryptionCredentials(credentials)
                      .build();
    }
}
