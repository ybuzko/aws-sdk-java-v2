package software.amazon.awssdk.services.s3.encryption.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.crypto.SecretKey;
import software.amazon.awssdk.services.s3.encryption.EncryptionContext;
import software.amazon.awssdk.services.s3.encryption.EncryptionPolicy;
import software.amazon.awssdk.services.s3.encryption.S3EncryptionRuntime;
import software.amazon.awssdk.services.s3.encryption.auth.EncryptionCredentials;
import software.amazon.awssdk.services.s3.encryption.auth.ResolveKeyRequest;
import software.amazon.awssdk.services.s3.encryption.auth.ResolveKeyResponse;
import software.amazon.awssdk.services.s3.encryption.content.Content;
import software.amazon.awssdk.services.s3.encryption.content.ContentEncryptor;
import software.amazon.awssdk.services.s3.encryption.content.DecryptObjectRequest;
import software.amazon.awssdk.services.s3.encryption.content.DecryptObjectResponse;
import software.amazon.awssdk.services.s3.encryption.content.EncryptObjectRequest;
import software.amazon.awssdk.services.s3.encryption.content.EncryptObjectResponse;
import software.amazon.awssdk.services.s3.encryption.keywrap.DecryptKeyResponse;
import software.amazon.awssdk.services.s3.encryption.keywrap.EncryptKeyResponse;
import software.amazon.awssdk.services.s3.encryption.keywrap.EncryptedSecretKey;
import software.amazon.awssdk.services.s3.encryption.keywrap.KeyWrapAlgorithm;
import software.amazon.awssdk.services.s3.encryption.metadata.MetadataKey;

public class DefaultS3EncryptionRuntime implements S3EncryptionRuntime {
    private final KeyGeneratorProvider contentKeyGenerator;
    private final EncryptionPolicy encryptionPolicy;
    private final List<EncryptionCredentials> additionalReadCredentialsProviders;
    private final EncryptionCredentials readWriteCredentialsProvider;
    private final ContentEncryptor encryptor;
    private final KeyWrapAlgorithm keyWrapAlgorithm;

    public DefaultS3EncryptionRuntime() {
        this.contentKeyGenerator = null;
        this.encryptionPolicy = null;
        this.additionalReadCredentialsProviders = null;
        this.readWriteCredentialsProvider = null;

        this.encryptor = encryptionPolicy.preferredContentEncryptionAlgorithm().createContentEncryptor();
        this.keyWrapAlgorithm = keyEncryptionAlgorithm();
    }

    private KeyWrapAlgorithm keyEncryptionAlgorithm() {
        Set<KeyWrapAlgorithm> supportedAlgorithms =
            readWriteCredentialsProvider.supportedKeyWrapAlgorithms();
        List<KeyWrapAlgorithm> preferredAlgorithms =
            encryptionPolicy.preferredKeyEncryptionAlgorithms();

        for (KeyWrapAlgorithm algorithm : preferredAlgorithms) {
            if (supportedAlgorithms.contains(algorithm)) {
                return algorithm;
            }
        }

        throw new IllegalStateException("No preferred key encryption algorithm (" + preferredAlgorithms + ") is included in the "
                                        + "set supported by the credentials provider (" + supportedAlgorithms + ").");
    }

    @Override
    public DecryptObjectResponse decryptObject(DecryptObjectRequest request) {
        EncryptionContext context = createContext(c -> c.metadata(request.metadata()));

        ResolveKeyResponse contentKeyResponse =
            resolveKey(ResolveKeyRequest.builder()
                                        .context(context)
                                        .algorithm(keyWrapAlgorithm)
                                        .credentialsProvider(getReadCredentialsProvider(request.metadata()))
                                        .build());

        context.copy(c -> c.contentEncryptionKey(contentKeyResponse.secretKey()));

        Content decryptedContent = encryptor.decryptContent(r -> r.ciphertext(request.ciphertext())
                                                                  .context(context))
                                            .plaintext();

        return DecryptObjectResponse.builder()
                                    .plaintext(decryptedContent)
                                    .contentType(null) // TODO : Restore from metadata
                                    .contentEncoding(null) // TODO : Restore from metadata
                                    .build();
    }

    @Override
    public EncryptObjectResponse encryptObject(EncryptObjectRequest request) {
        EncryptionContext context = createContext(c -> c.metadata(request.metadata()));

        ResolveKeyResponse contentKeyResponse =
            resolveKey(ResolveKeyRequest.builder()
                                        .context(context)
                                        .algorithm(keyWrapAlgorithm)
                                        .credentialsProvider(readWriteCredentialsProvider)
                                        .build());

        context.copy(c -> {
            c.contentEncryptionKey(contentKeyResponse.secretKey());
            contentKeyResponse.newMetadata().forEach(c::putMetadata);
        });

        Content encryptedContent = encryptor.encryptContent(r -> r.plaintext(request.plaintext())
                                                                  .context(context))
                                            .ciphertext();

        return EncryptObjectResponse.builder()
                                    .ciphertext(encryptedContent)
                                    .metadata(request.metadata()) // TODO : Save original content-type to metadata
                                    .contentType("application/octet-stream")
                                    .contentEncoding(null) // TODO : What encoding is appropriate?
                                    .build();
    }

    private EncryptionCredentials getReadCredentialsProvider(Map<String, String> metadata) {
        String keyWrapAlgorithm = MetadataKey.KEY_WRAP_ALGORITHM.read(metadata);

        for (EncryptionCredentials provider : additionalReadCredentialsProviders) {
            if (provider.supportedKeyWrapAlgorithms()
                        .stream()
                        .map(KeyWrapAlgorithm::name)
                        .anyMatch(keyWrapAlgorithm::equals)) {
                return provider;
            }
        }

        if (readWriteCredentialsProvider.supportedKeyWrapAlgorithms()
                                        .stream()
                                        .map(KeyWrapAlgorithm::name)
                                        .anyMatch(keyWrapAlgorithm::equals)) {
            return readWriteCredentialsProvider;
        }

        throw new IllegalStateException("No matching credentials for key wrap algorithm: " + keyWrapAlgorithm);
    }

    private ResolveKeyResponse resolveKey(ResolveKeyRequest request) {
        EncryptedSecretKey encryptedKey = request.context().metadata(MetadataKey.ENCRYPTED_SECRET_KEY);

        if (encryptedKey == null) {
            return createNewKey(request);
        }

        return decryptKey(request, encryptedKey);
    }

    private ResolveKeyResponse createNewKey(ResolveKeyRequest request) {
        SecretKey newKey = encryptionPolicy.keyGeneratorProvider().createKeyGenerator().generateKey();
        EncryptKeyResponse encryptResponse =
            request.credentialsProvider().encryptKey(r -> r.key(newKey)
                                                           .keyWrapAlgorithm(request.keyWrapAlgorithm())
                                                           .context(request.context()));


        return ResolveKeyResponse.builder()
                                 .secretKey(newKey)
                                 .putNewMetadata(MetadataKey.ENCRYPTED_SECRET_KEY, encryptResponse.key())
                                 .putNewMetadata(MetadataKey.KEY_ALGORITHM, newKey.getAlgorithm())
                                 .putNewMetadata(MetadataKey.KEY_WRAP_ALGORITHM, request.keyWrapAlgorithm().name())
                                 .build();
    }

    private ResolveKeyResponse decryptKey(ResolveKeyRequest request, EncryptedSecretKey encryptedKey) {
        DecryptKeyResponse decryptResponse = request.credentialsProvider().decryptKey(r -> r.key(encryptedKey));

        return ResolveKeyResponse.builder()
                                 .secretKey(decryptResponse.key())
                                 .putNewMetadata(MetadataKey.ENCRYPTED_SECRET_KEY, encryptedKey)
                                 .putNewMetadata(MetadataKey.KEY_ALGORITHM, encryptedKey.keyAlgorithm())
                                 .putNewMetadata(MetadataKey.KEY_WRAP_ALGORITHM, request.keyWrapAlgorithm().name())
                                 .build();
    }

    private EncryptionContext createContext(Consumer<EncryptionContext.Builder> consumer) {
        EncryptionContext.Builder result = EncryptionContext.builder()
                                                            .encryptionPolicy(encryptionPolicy);
        consumer.accept(result);
        return result.build();
    }

    @Override
    public void close() {
        // TODO
    }
}
