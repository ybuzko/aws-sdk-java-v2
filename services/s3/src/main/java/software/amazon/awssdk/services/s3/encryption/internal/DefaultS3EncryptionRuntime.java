package software.amazon.awssdk.services.s3.encryption.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.crypto.SecretKey;
import software.amazon.awssdk.services.s3.encryption.auth.ResolveKeyRequest;
import software.amazon.awssdk.services.s3.encryption.content.Content;
import software.amazon.awssdk.services.s3.encryption.content.ContentEncryptor;
import software.amazon.awssdk.services.s3.encryption.EncryptionContext;
import software.amazon.awssdk.services.s3.encryption.auth.EncryptionCredentialsProvider;
import software.amazon.awssdk.services.s3.encryption.EncryptionPolicy;
import software.amazon.awssdk.services.s3.encryption.keywrap.DecryptKeyResponse;
import software.amazon.awssdk.services.s3.encryption.keywrap.EncryptKeyResponse;
import software.amazon.awssdk.services.s3.encryption.keywrap.EncryptedSecretKey;
import software.amazon.awssdk.services.s3.encryption.keywrap.KeyEncryptor;
import software.amazon.awssdk.services.s3.encryption.keywrap.KeyWrapAlgorithm;
import software.amazon.awssdk.services.s3.encryption.S3EncryptionRuntime;
import software.amazon.awssdk.services.s3.encryption.metadata.MetadataKey;
import software.amazon.awssdk.services.s3.encryption.content.DecryptContentRequest;
import software.amazon.awssdk.services.s3.encryption.content.DecryptContentResponse;
import software.amazon.awssdk.services.s3.encryption.content.EncryptContentRequest;
import software.amazon.awssdk.services.s3.encryption.content.EncryptContentResponse;
import software.amazon.awssdk.services.s3.encryption.auth.ResolveKeyResponse;

public class DefaultS3EncryptionRuntime implements S3EncryptionRuntime {
    private final KeyGeneratorProvider contentKeyGenerator;
    private final EncryptionPolicy encryptionPolicy;
    private final List<EncryptionCredentialsProvider> additionalReadCredentialsProviders;
    private final EncryptionCredentialsProvider readWriteCredentialsProvider;
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
    public DecryptContentResponse decryptContent(DecryptContentRequest request) {
        EncryptionContext context = createContext(c -> c.metadata(request.metadata()));

        ResolveKeyResponse contentKeyResponse =
            resolveKey(ResolveKeyRequest.builder()
                                        .context(context)
                                        .algorithm(keyWrapAlgorithm)
                                        .credentialsProvider(getReadCredentialsProvider(request.metadata()))
                                        .build());

        context.copy(c -> c.contentEncryptionKey(contentKeyResponse.secretKey()));

        Content decryptedContent = encryptor.decryptContent(request.content(), context);

        return DecryptContentResponse.builder()
                                     .content(decryptedContent)
                                     .contentType(null) // TODO : Restore from metadata
                                     .contentEncoding(null) // TODO : Restore from metadata
                                     .build();
    }

    @Override
    public EncryptContentResponse encryptContent(EncryptContentRequest request) {
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

        Content encryptedContent = encryptor.encryptContent(request.content(), context);

        return EncryptContentResponse.builder()
                                     .content(encryptedContent)
                                     .metadata(request.metadata()) // TODO : Save original content-type to metadata
                                     .contentType("application/octet-stream")
                                     .contentEncoding(null) // TODO : What encoding is appropriate?
                                     .build();
    }

    private EncryptionCredentialsProvider getReadCredentialsProvider(Map<String, String> metadata) {
        String keyWrapAlgorithm = MetadataKey.KEY_WRAP_ALGORITHM.read(metadata);

        for (EncryptionCredentialsProvider provider : additionalReadCredentialsProviders) {
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
            // TODO: Why indirection? Why not credentials decrypt/encrypt?
            return createNewKey(request);
        }

        return decryptKey(request, encryptedKey);
    }

    private ResolveKeyResponse createNewKey(ResolveKeyRequest request) {
        SecretKey newKey = encryptionPolicy.keyGeneratorProvider().createKeyGenerator().generateKey();
        KeyEncryptor keyEncryptor = request.credentialsProvider().createKeyEncryptor(request.algorithm());
        EncryptKeyResponse encryptResponse = keyEncryptor.encryptKey(r -> r.key(newKey).context(request.context()));


        return ResolveKeyResponse.builder()
                                 .secretKey(newKey)
                                 .putNewMetadata(MetadataKey.ENCRYPTED_SECRET_KEY, encryptResponse.key())
                                 .putNewMetadata(MetadataKey.KEY_ALGORITHM, newKey.getAlgorithm())
                                 .putNewMetadata(MetadataKey.KEY_WRAP_ALGORITHM, request.algorithm().name())
                                 .build();
    }

    private ResolveKeyResponse decryptKey(ResolveKeyRequest request, EncryptedSecretKey encryptedKey) {
        KeyEncryptor keyEncryptor = request.credentialsProvider().createKeyEncryptor(request.algorithm());
        DecryptKeyResponse decryptResponse = keyEncryptor.decryptKey(r -> r.key(encryptedKey));

        return ResolveKeyResponse.builder()
                                 .secretKey(decryptResponse.key())
                                 .putNewMetadata(MetadataKey.ENCRYPTED_SECRET_KEY, encryptedKey)
                                 .putNewMetadata(MetadataKey.KEY_ALGORITHM, encryptedKey.keyAlgorithm())
                                 .putNewMetadata(MetadataKey.KEY_WRAP_ALGORITHM, request.algorithm().name())
                                 .build();
    }

    private EncryptionContext createContext(Consumer<EncryptionContext.Builder> consumer) {
        EncryptionContext.Builder result = EncryptionContext.builder()
                                                            .encryptionPolicy(encryptionPolicy);
        consumer.accept(result);
        return result.build();
    }
}
