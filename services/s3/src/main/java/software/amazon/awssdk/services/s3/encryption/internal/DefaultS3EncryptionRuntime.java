package software.amazon.awssdk.services.s3.encryption.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import software.amazon.awssdk.services.s3.encryption.Content;
import software.amazon.awssdk.services.s3.encryption.ContentEncryptor;
import software.amazon.awssdk.services.s3.encryption.ContentKeyEncryptionAlgorithm;
import software.amazon.awssdk.services.s3.encryption.ContentKeyGenerator;
import software.amazon.awssdk.services.s3.encryption.EncryptionContext;
import software.amazon.awssdk.services.s3.encryption.EncryptionCredentialsProvider;
import software.amazon.awssdk.services.s3.encryption.EncryptionPolicy;
import software.amazon.awssdk.services.s3.encryption.S3EncryptionRuntime;
import software.amazon.awssdk.services.s3.encryption.metadata.MetadataKey;
import software.amazon.awssdk.services.s3.encryption.model.DecryptContentRequest;
import software.amazon.awssdk.services.s3.encryption.model.DecryptContentResponse;
import software.amazon.awssdk.services.s3.encryption.model.EncryptContentRequest;
import software.amazon.awssdk.services.s3.encryption.model.EncryptContentResponse;
import software.amazon.awssdk.services.s3.encryption.model.ResolveKeyResponse;

public class DefaultS3EncryptionRuntime implements S3EncryptionRuntime {
    private final ContentKeyGenerator contentKeyGenerator;
    private final EncryptionPolicy encryptionPolicy;
    private final List<EncryptionCredentialsProvider> additionalReadCredentialsProviders;
    private final EncryptionCredentialsProvider readWriteCredentialsProvider;
    private final ContentEncryptor encryptor;
    private final ContentKeyEncryptionAlgorithm keyEncryptionAlgorithm;

    public DefaultS3EncryptionRuntime() {
        this.contentKeyGenerator = null;
        this.encryptionPolicy = null;
        this.additionalReadCredentialsProviders = null;
        this.readWriteCredentialsProvider = null;

        this.encryptor = encryptionPolicy.preferredContentEncryptionAlgorithm().createContentEncryptor();
        this.keyEncryptionAlgorithm = keyEncryptionAlgorithm();
    }

    private ContentKeyEncryptionAlgorithm keyEncryptionAlgorithm() {
        Set<ContentKeyEncryptionAlgorithm> supportedAlgorithms =
            readWriteCredentialsProvider.supportedContentEncryptionKeyAlgorithms();
        List<ContentKeyEncryptionAlgorithm> preferredAlgorithms =
            encryptionPolicy.preferredKeyEncryptionAlgorithms();

        for (ContentKeyEncryptionAlgorithm algorithm : preferredAlgorithms) {
            if (supportedAlgorithms.contains(algorithm)) {
                return algorithm;
            }
        }

        throw new IllegalStateException("No preferred key encryption algorithm (" + preferredAlgorithms + ") is included in the "
                                        + "set supported by the credentials provider (" + supportedAlgorithms + ").")
    }

    @Override
    public DecryptContentResponse decryptContent(DecryptContentRequest request) {
        EncryptionContext context = createContext(c -> c.metadata(request.metadata()));

        EncryptionCredentialsProvider credentialsProvider = getReadCredentialsProvider(request.metadata());

        ResolveKeyResponse contentKeyResponse =
            credentialsProvider.resolveContentKey(r -> r.context(context)
                                                        .algorithm(keyEncryptionAlgorithm));

        context.copy(c -> c.contentEncryptionKey(contentKeyResponse.secretKey()));

        Content decryptedContent = encryptor.decryptContent(request.content(), context);

        return DecryptContentResponse.builder()
                                     .content(decryptedContent)
                                     .contentType(null) // TODO : Restore from metadata
                                     .contentEncoding(null) // TODO : Restore from metadata
                                     .build();
    }

    private EncryptionCredentialsProvider getReadCredentialsProvider(Map<String, String> metadata) {
        String keyWrapAlgorithm = MetadataKey.KEY_WRAP_ALGORITHM.read(metadata);

        for (EncryptionCredentialsProvider provider : additionalReadCredentialsProviders) {
            if (provider.supportedContentEncryptionKeyAlgorithms()
                        .stream()
                        .map(ContentKeyEncryptionAlgorithm::name)
                        .anyMatch(keyWrapAlgorithm::equals)) {
                return provider;
            }
        }

        if (readWriteCredentialsProvider.supportedContentEncryptionKeyAlgorithms()
                                        .stream()
                                        .map(ContentKeyEncryptionAlgorithm::name)
                                        .anyMatch(keyWrapAlgorithm::equals)) {
            return readWriteCredentialsProvider;
        }

        throw new IllegalStateException("No matching credentials for key wrap algorithm: " + keyWrapAlgorithm);
    }

    @Override
    public EncryptContentResponse encryptContent(EncryptContentRequest request) {
        EncryptionContext context = createContext(c -> c.metadata(request.metadata()));

        ResolveKeyResponse contentKeyResponse =
            readWriteCredentialsProvider.resolveContentKey(r -> r.context(context)
                                                                 .algorithm(keyEncryptionAlgorithm));

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

    private EncryptionContext createContext(Consumer<EncryptionContext.Builder> consumer) {
        EncryptionContext.Builder result = EncryptionContext.builder()
                                                            .encryptionPolicy(encryptionPolicy);
        consumer.accept(result);
        return result.build();
    }
}
