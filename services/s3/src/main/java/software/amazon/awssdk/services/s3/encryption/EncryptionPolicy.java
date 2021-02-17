package software.amazon.awssdk.services.s3.encryption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import software.amazon.awssdk.services.s3.encryption.content.EncryptionAlgorithm;
import software.amazon.awssdk.services.s3.encryption.internal.KeyGeneratorProvider;
import software.amazon.awssdk.services.s3.encryption.keywrap.KeyWrapAlgorithm;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface EncryptionPolicy extends ToCopyableBuilder<EncryptionPolicy.Builder, EncryptionPolicy> {
    @Deprecated
    EncryptionPolicy LEGACY =
        builder().allowedKeyEncryptionAlgorithms(KeyWrapAlgorithm.AES_GCM,
                                                 KeyWrapAlgorithm.KMS,
                                                 KeyWrapAlgorithm.RSA_OAEP_SHA1,
                                                 KeyWrapAlgorithm.KMS_NO_CONTEXT,
                                                 KeyWrapAlgorithm.AES,
                                                 KeyWrapAlgorithm.RSA_ECB_OAEP_SHA256_MGF1_PADDING)
                 .preferredKeyEncryptionAlgorithms(KeyWrapAlgorithm.KMS,
                                                   KeyWrapAlgorithm.AES_GCM,
                                                   KeyWrapAlgorithm.RSA_OAEP_SHA1)
                 .allowedContentEncryptionAlgorithms(EncryptionAlgorithm.AES_CBC,
                                                     EncryptionAlgorithm.AES_GCM,
                                                     EncryptionAlgorithm.AES_CTR)
                 .preferredContentEncryptionAlgorithm(EncryptionAlgorithm.AES_GCM)
                 .contentKeyGenerator(KeyGeneratorProvider.AES_256)
                 .build();

    EncryptionPolicy V2020_02_16 =
        builder().allowedKeyEncryptionAlgorithms(KeyWrapAlgorithm.AES_GCM,
                                                 KeyWrapAlgorithm.KMS,
                                                 KeyWrapAlgorithm.RSA_OAEP_SHA1)
                 .preferredKeyEncryptionAlgorithms(KeyWrapAlgorithm.KMS,
                                                   KeyWrapAlgorithm.AES_GCM,
                                                   KeyWrapAlgorithm.RSA_OAEP_SHA1)
                 .allowedContentEncryptionAlgorithms(EncryptionAlgorithm.AES_GCM,
                                                     EncryptionAlgorithm.AES_CBC)
                 .preferredContentEncryptionAlgorithm(EncryptionAlgorithm.AES_GCM)
                 .contentKeyGenerator(KeyGeneratorProvider.AES_256)
                 .build();

    List<KeyWrapAlgorithm> allowedKeyEncryptionAlgorithms();
    List<KeyWrapAlgorithm> preferredKeyEncryptionAlgorithms();
    List<EncryptionAlgorithm> allowedContentEncryptionAlgorithms();
    EncryptionAlgorithm preferredContentEncryptionAlgorithm();
    KeyGeneratorProvider keyGeneratorProvider();

    static Builder builder() {
        return null;
    }

    static EncryptionPolicy migratingBetween(EncryptionPolicy from, EncryptionPolicy to) {
        List<KeyWrapAlgorithm> allowedKeyAlgorithms = new ArrayList<>();
        allowedKeyAlgorithms.addAll(from.allowedKeyEncryptionAlgorithms());
        allowedKeyAlgorithms.addAll(to.allowedKeyEncryptionAlgorithms());

        List<EncryptionAlgorithm> allowedContentAlgorithms = new ArrayList<>();
        allowedContentAlgorithms.addAll(from.allowedContentEncryptionAlgorithms());
        allowedContentAlgorithms.addAll(to.allowedContentEncryptionAlgorithms());

        return EncryptionPolicy.builder()
                               .allowedKeyEncryptionAlgorithms(allowedKeyAlgorithms)
                               .allowedContentEncryptionAlgorithms(allowedContentAlgorithms)
                               .preferredKeyEncryptionAlgorithms(to.preferredKeyEncryptionAlgorithms())
                               .preferredContentEncryptionAlgorithm(to.preferredContentEncryptionAlgorithm())
                               .contentKeyGenerator(to.keyGeneratorProvider())
                               .build();
    }

    interface Builder extends CopyableBuilder<Builder, EncryptionPolicy> {
        Builder allowedKeyEncryptionAlgorithms(KeyWrapAlgorithm... allowedKeyWrapAlgorithms);
        Builder allowedKeyEncryptionAlgorithms(Collection<KeyWrapAlgorithm> allowedKeyWrapAlgorithms);
        Builder addAllowedKeyEncryptionAlgorithm(KeyWrapAlgorithm allowedKeyWrapAlgorithm);
        Builder clearAllowedKeyEncryptionAlgorithms();

        Builder preferredKeyEncryptionAlgorithms(KeyWrapAlgorithm... preferredKeyWrapAlgorithms);
        Builder preferredKeyEncryptionAlgorithms(Collection<KeyWrapAlgorithm> preferredKeyWrapAlgorithms);
        Builder addPreferredKeyEncryptionAlgorithm(KeyWrapAlgorithm preferredKeyWrapAlgorithm);
        Builder clearPreferredKeyEncryptionAlgorithms();

        Builder allowedContentEncryptionAlgorithms(EncryptionAlgorithm... readEncryptionAlgorithms);
        Builder allowedContentEncryptionAlgorithms(Collection<EncryptionAlgorithm> readEncryptionAlgorithms);
        Builder addAllowedContentEncryptionAlgorithm(EncryptionAlgorithm readEncryptionAlgorithm);
        Builder clearAllowedContentEncryptionAlgorithms();

        Builder preferredContentEncryptionAlgorithm(EncryptionAlgorithm preferredEncryptionAlgorithm);

        Builder contentKeyGenerator(KeyGeneratorProvider keyGeneratorSupplier);

        EncryptionPolicy build();

    }
}
