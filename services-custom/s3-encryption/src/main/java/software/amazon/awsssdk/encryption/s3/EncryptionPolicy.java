package software.amazon.awsssdk.encryption.s3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import software.amazon.awsssdk.encryption.s3.auth.EncryptionCredentials;
import software.amazon.awsssdk.encryption.s3.content.ContentEncryptionAlgorithm;
import software.amazon.awsssdk.encryption.s3.internal.KeyGeneratorProvider;
import software.amazon.awsssdk.encryption.s3.keywrap.KeyWrapAlgorithm;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

/**
 * The {@link EncryptionPolicy} defines which algorithms a {@link S3EncryptionClient} or {@link S3EncryptionRuntime} can use.
 *
 * <p>The SDK provides multiple built-in encryption policies, which most users will find sufficient:
 * <ol>
 *     <li>{@link #V2020_02_16} (recommended)</li>
 *     <li>{@link #LEGACY} (deprecated)</li>
 * </ol>
 *
 * <p>You may define additional encryption policies using {@link #builder()} or {@link #migratingBetween}.
 */
public interface EncryptionPolicy extends ToCopyableBuilder<EncryptionPolicy.Builder, EncryptionPolicy> {
    /**
     * The currently-recommended encryption policy, that only permits and uses encryption algorithms that are known to be secure
     */
    EncryptionPolicy V2020_02_16 =
        builder().allowedKeyWrapAlgorithms(KeyWrapAlgorithm.AES_GCM,
                                                 KeyWrapAlgorithm.KMS,
                                                 KeyWrapAlgorithm.RSA_OAEP_SHA1)
                 .preferredKeyWrapAlgorithms(KeyWrapAlgorithm.KMS,
                                                   KeyWrapAlgorithm.AES_GCM,
                                                   KeyWrapAlgorithm.RSA_OAEP_SHA1)
                 .allowedContentEncryptionAlgorithms(ContentEncryptionAlgorithm.AES_GCM,
                                                     ContentEncryptionAlgorithm.AES_CBC)
                 .preferredContentEncryptionAlgorithm(ContentEncryptionAlgorithm.AES_GCM)
                 .contentKeyGenerator(KeyGeneratorProvider.AES_256)
                 .build();

    /**
     * An encryption policy that supports reading any encryption algorithms used by the 1.x AWS SDK for Java throughout its
     * lifecycle. When migrating from old 1.x clients, you can use this policy until you have re-encrypted your existing objects.
     *
     * <p>We DO NOT recommend using this encryption policy for longer than it takes to re-encrypt your objects, because some of
     * the encryption schemes it uses are considered less secure.
     */
    @Deprecated
    EncryptionPolicy LEGACY =
        builder().allowedKeyWrapAlgorithms(KeyWrapAlgorithm.AES_GCM,
                                                 KeyWrapAlgorithm.KMS,
                                                 KeyWrapAlgorithm.RSA_OAEP_SHA1,
                                                 KeyWrapAlgorithm.KMS_NO_CONTEXT,
                                                 KeyWrapAlgorithm.AES,
                                                 KeyWrapAlgorithm.RSA_ECB_OAEP_SHA256_MGF1_PADDING)
                 .preferredKeyWrapAlgorithms(KeyWrapAlgorithm.KMS,
                                                   KeyWrapAlgorithm.AES_GCM,
                                                   KeyWrapAlgorithm.RSA_OAEP_SHA1)
                 .allowedContentEncryptionAlgorithms(ContentEncryptionAlgorithm.AES_CBC,
                                                     ContentEncryptionAlgorithm.AES_GCM,
                                                     ContentEncryptionAlgorithm.AES_CTR)
                 .preferredContentEncryptionAlgorithm(ContentEncryptionAlgorithm.AES_GCM)
                 .contentKeyGenerator(KeyGeneratorProvider.AES_256)
                 .build();

    /**
     * The value set via {@link Builder#allowedKeyWrapAlgorithms}.
     */
    List<KeyWrapAlgorithm> allowedKeyWrapAlgorithms();

    /**
     * The value set via {@link Builder#preferredKeyWrapAlgorithms}.
     */
    List<KeyWrapAlgorithm> preferredKeyWrapAlgorithms();

    /**
     * The value set via {@link Builder#allowedContentEncryptionAlgorithms}.
     */
    List<ContentEncryptionAlgorithm> allowedContentEncryptionAlgorithms();

    /**
     * The value set via {@link Builder#preferredContentEncryptionAlgorithm}.
     */
    ContentEncryptionAlgorithm preferredContentEncryptionAlgorithm();

    /**
     * The value set via {@link Builder#keyGeneratorProvider}.
     */
    KeyGeneratorProvider keyGeneratorProvider();

    /**
     * Create an {@link EncryptionPolicy.Builder} for defining your own encryption policy.
     *
     * <p>It's recommended that most users use a pre-defined policy via a static field on this class.</p>
     *
     * <p>This is useful when you want to limit the permitted policies even further than the included policies, or if you are
     * defining your own encryption algorithms.
     */
    static Builder builder() {
        return new DefaultEncryptionPolicy.Builder();
    }

    /**
     * Create an {@link EncryptionPolicy} that can read any objects that can be read by the "from" policy, and will write objects
     * using the recommended algorithms in the "to" policy.
     *
     * <p>This is useful for temporarily migrating between policies. After objects have been re-encrypted, you should switch to
     * using the "to" policy so that you're limiting the algorithms that you support reading.
     */
    static EncryptionPolicy migratingBetween(EncryptionPolicy from, EncryptionPolicy to) {
        List<KeyWrapAlgorithm> allowedKeyAlgorithms = new ArrayList<>();
        allowedKeyAlgorithms.addAll(from.allowedKeyWrapAlgorithms());
        allowedKeyAlgorithms.addAll(to.allowedKeyWrapAlgorithms());

        List<ContentEncryptionAlgorithm> allowedContentAlgorithms = new ArrayList<>();
        allowedContentAlgorithms.addAll(from.allowedContentEncryptionAlgorithms());
        allowedContentAlgorithms.addAll(to.allowedContentEncryptionAlgorithms());

        return EncryptionPolicy.builder()
                               .allowedKeyWrapAlgorithms(allowedKeyAlgorithms)
                               .allowedContentEncryptionAlgorithms(allowedContentAlgorithms)
                               .preferredKeyWrapAlgorithms(to.preferredKeyWrapAlgorithms())
                               .preferredContentEncryptionAlgorithm(to.preferredContentEncryptionAlgorithm())
                               .contentKeyGenerator(to.keyGeneratorProvider())
                               .build();
    }

    /**
     * Created via {@link #builder()}, this object can configure and create {@link EncryptionPolicy}s.
     */
    interface Builder extends CopyableBuilder<Builder, EncryptionPolicy> {
        /**
         * Set which key wrap algorithms this policy allows using for reading and writing credentials.
         * 
         * <p>In the ideal world, this would always match the {@link #preferredKeyWrapAlgorithms()}, so that the number of
         * algorithms used by the client are as limited as possible.
         */
        Builder allowedKeyWrapAlgorithms(KeyWrapAlgorithm... allowedKeyWrapAlgorithms);

        /**
         * Set which key wrap algorithms this policy allows using for reading and writing credentials.
         *
         * @see #allowedKeyWrapAlgorithms(KeyWrapAlgorithm...)
         */
        Builder allowedKeyWrapAlgorithms(Collection<KeyWrapAlgorithm> allowedKeyWrapAlgorithms);

        /**
         * Set which key wrap algorithms this policy prefers to use for writing credentials.
         *
         * <p>While {@link #allowedKeyWrapAlgorithms} specifies which key encryption algorithms are permitted for write
         * AND read, the preferred algorithms are the only algorithms that will be used for writing.
         *
         * <p>This set must include at least one algorithm supported by your {@link EncryptionCredentials}. If multiple
         * preferred algorithms are supported by your {code EncryptionCredentials}, then the first preferred algorithm in this
         * list will be used.
         */
        Builder preferredKeyWrapAlgorithms(KeyWrapAlgorithm... preferredKeyWrapAlgorithms);

        /**
         * Set which key wrap algorithms this policy prefers to use for writing credentials.
         * 
         * @see #preferredKeyWrapAlgorithms(KeyWrapAlgorithm...)
         */
        Builder preferredKeyWrapAlgorithms(Collection<KeyWrapAlgorithm> preferredKeyWrapAlgorithms);

        /**
         * Set which content encryption algorithms this policy allows using for reading and writing content.
         *
         * <p>In the ideal world, this would always match the {@link #preferredContentEncryptionAlgorithm()}, so that the number
         * of algorithms used by the client are as limited as possible.
         */
        Builder allowedContentEncryptionAlgorithms(ContentEncryptionAlgorithm... readContentEncryptionAlgorithms);

        /**
         * Set which content encryption algorithms this policy allows using for reading and writing content.
         *
         * @see #allowedContentEncryptionAlgorithms(ContentEncryptionAlgorithm...)
         */
        Builder allowedContentEncryptionAlgorithms(Collection<ContentEncryptionAlgorithm> readContentEncryptionAlgorithms);

        /**
         * Set which content encryption algorithms this policy prefers to use for encrypting object content.
         *
         * <p>While {@link #allowedContentEncryptionAlgorithms} specifies which key encryption algorithms are permitted for
         * write AND read, the preferred algorithm is the only algorithm that will be used for writing.
         *
         * <p>This encryption algorithm must support using the type of key generated by the configured
         * {@link #contentKeyGenerator(KeyGeneratorProvider)}.
         */
        Builder preferredContentEncryptionAlgorithm(ContentEncryptionAlgorithm preferredContentEncryptionAlgorithm);

        /**
         * Set the generator that should be used for generating content keys.
         *
         * <p>This key type should be one supported by your {@link #preferredContentEncryptionAlgorithm(ContentEncryptionAlgorithm)}.
         */
        Builder contentKeyGenerator(KeyGeneratorProvider keyGeneratorSupplier);

        /**
         * Create an {@link EncryptionPolicy} using the configuration on this builder.
         */
        EncryptionPolicy build();
    }
}
