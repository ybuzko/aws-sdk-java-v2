package software.amazon.awsssdk.encryption.s3.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.crypto.SecretKey;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awsssdk.encryption.s3.EncryptionContext;
import software.amazon.awsssdk.encryption.s3.EncryptionPolicy;
import software.amazon.awsssdk.encryption.s3.S3EncryptionClient;
import software.amazon.awsssdk.encryption.s3.S3EncryptionRuntime;
import software.amazon.awsssdk.encryption.s3.auth.EncryptionCredentials;
import software.amazon.awsssdk.encryption.s3.auth.ResolveKeyRequest;
import software.amazon.awsssdk.encryption.s3.auth.ResolveKeyResponse;
import software.amazon.awsssdk.encryption.s3.content.Content;
import software.amazon.awsssdk.encryption.s3.content.ContentEncryptor;
import software.amazon.awsssdk.encryption.s3.content.DecryptObjectRequest;
import software.amazon.awsssdk.encryption.s3.content.DecryptObjectResponse;
import software.amazon.awsssdk.encryption.s3.content.EncryptObjectRequest;
import software.amazon.awsssdk.encryption.s3.content.EncryptObjectResponse;
import software.amazon.awsssdk.encryption.s3.keywrap.DecryptKeyResponse;
import software.amazon.awsssdk.encryption.s3.keywrap.EncryptKeyResponse;
import software.amazon.awsssdk.encryption.s3.keywrap.EncryptedSecretKey;
import software.amazon.awsssdk.encryption.s3.keywrap.KeyWrapAlgorithm;
import software.amazon.awsssdk.encryption.s3.metadata.MetadataKey;

public class DefaultS3EncryptionRuntime implements S3EncryptionRuntime {
    private final KeyGeneratorProvider contentKeyGenerator;
    private final EncryptionPolicy encryptionPolicy;
    private final List<EncryptionCredentials> additionalReadCredentialsProviders;
    private final EncryptionCredentials readWriteCredentialsProvider;
    private final ContentEncryptor encryptor;
    private final KeyWrapAlgorithm keyWrapAlgorithm;

    public DefaultS3EncryptionRuntime(Builder builder) {
        this.encryptionPolicy = builder.encryptionPolicy;
        this.contentKeyGenerator = builder.encryptionPolicy.keyGeneratorProvider();
        this.encryptor = builder.encryptionPolicy.preferredContentEncryptionAlgorithm().createContentEncryptor();
        this.additionalReadCredentialsProviders = builder.readEncryptionCredentials;
        this.readWriteCredentialsProvider = builder.encryptionCredentials;

        this.keyWrapAlgorithm = keyEncryptionAlgorithm();
    }

    public static S3EncryptionRuntime.Builder builder(EncryptionPolicy encryptionPolicy) {
        return new Builder(encryptionPolicy);
    }

    private KeyWrapAlgorithm keyEncryptionAlgorithm() {
        Set<KeyWrapAlgorithm> supportedAlgorithms = readWriteCredentialsProvider.supportedKeyWrapAlgorithms();
        List<KeyWrapAlgorithm> preferredAlgorithms = encryptionPolicy.preferredKeyWrapAlgorithms();

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
        IoUtils.closeIfCloseable(this.readWriteCredentialsProvider, null);
        this.additionalReadCredentialsProviders.forEach(p -> IoUtils.closeIfCloseable(p, null));
    }

    @Override
    public S3EncryptionRuntime.Builder toBuilder() {
        return builder(encryptionPolicy).readEncryptionCredentials(this.additionalReadCredentialsProviders)
                                        .encryptionCredentials(this.readWriteCredentialsProvider);
    }

    private static class Builder implements S3EncryptionRuntime.Builder {
        private final EncryptionPolicy encryptionPolicy;
        private final List<EncryptionCredentials> readEncryptionCredentials = new ArrayList<>();
        private EncryptionCredentials encryptionCredentials;

        public Builder(EncryptionPolicy encryptionPolicy) {
            this.encryptionPolicy = encryptionPolicy;
        }

        @Override
        public Builder encryptionCredentials(EncryptionCredentials encryptionCredentials) {
            this.encryptionCredentials = encryptionCredentials;
            return this;
        }

        @Override
        public Builder readEncryptionCredentials(EncryptionCredentials... readEncryptionCredentials) {
            readEncryptionCredentials(Arrays.asList(readEncryptionCredentials));
            return this;
        }

        @Override
        public Builder readEncryptionCredentials(Collection<EncryptionCredentials> readEncryptionCredentials) {
            this.readEncryptionCredentials.clear();
            this.readEncryptionCredentials.addAll(readEncryptionCredentials);
            return this;
        }

        @Override
        public S3EncryptionRuntime build() {
            return new DefaultS3EncryptionRuntime(this);
        }
    }
}
