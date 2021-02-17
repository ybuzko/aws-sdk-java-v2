package software.amazon.awssdk.services.s3.encryption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface EncryptionPolicy extends ToCopyableBuilder<EncryptionPolicy.Builder, EncryptionPolicy> {
    @Deprecated
    EncryptionPolicy LEGACY =
        builder().allowedKeyEncryptionAlgorithms(ContentKeyEncryptionAlgorithm.AES_GCM,
                                                 ContentKeyEncryptionAlgorithm.KMS,
                                                 ContentKeyEncryptionAlgorithm.RSA_OAEP_SHA1,
                                                 ContentKeyEncryptionAlgorithm.KMS_NO_CONTEXT,
                                                 ContentKeyEncryptionAlgorithm.AES,
                                                 ContentKeyEncryptionAlgorithm.RSA_ECB_OAEP_SHA256_MGF1_PADDING)
                 .preferredKeyEncryptionAlgorithms(ContentKeyEncryptionAlgorithm.KMS,
                                                   ContentKeyEncryptionAlgorithm.AES_GCM,
                                                   ContentKeyEncryptionAlgorithm.RSA_OAEP_SHA1)
                 .allowedContentEncryptionAlgorithms(ContentEncryptionAlgorithm.AES_CBC,
                                                     ContentEncryptionAlgorithm.AES_GCM,
                                                     ContentEncryptionAlgorithm.AES_CTR)
                 .preferredContentEncryptionAlgorithm(ContentEncryptionAlgorithm.AES_GCM)
                 .contentKeyGenerator(ContentKeyGenerator.AES_256)
                 .build();

    EncryptionPolicy V2020_02_16 =
        builder().allowedKeyEncryptionAlgorithms(ContentKeyEncryptionAlgorithm.AES_GCM,
                                                 ContentKeyEncryptionAlgorithm.KMS,
                                                 ContentKeyEncryptionAlgorithm.RSA_OAEP_SHA1)
                 .preferredKeyEncryptionAlgorithms(ContentKeyEncryptionAlgorithm.KMS,
                                                   ContentKeyEncryptionAlgorithm.AES_GCM,
                                                   ContentKeyEncryptionAlgorithm.RSA_OAEP_SHA1)
                 .allowedContentEncryptionAlgorithms(ContentEncryptionAlgorithm.AES_GCM,
                                                     ContentEncryptionAlgorithm.AES_CBC)
                 .preferredContentEncryptionAlgorithm(ContentEncryptionAlgorithm.AES_GCM)
                 .contentKeyGenerator(ContentKeyGenerator.AES_256)
                 .build();

    List<ContentKeyEncryptionAlgorithm> allowedKeyEncryptionAlgorithms();
    List<ContentKeyEncryptionAlgorithm> preferredKeyEncryptionAlgorithms();
    List<ContentEncryptionAlgorithm> allowedContentEncryptionAlgorithms();
    ContentEncryptionAlgorithm preferredContentEncryptionAlgorithm();
    ContentKeyGenerator contentKeyGenerator();

    static Builder builder() {
        return null;
    }

    static EncryptionPolicy migratingBetween(EncryptionPolicy from, EncryptionPolicy to) {
        List<ContentKeyEncryptionAlgorithm> allowedKeyAlgorithms = new ArrayList<>();
        allowedKeyAlgorithms.addAll(from.allowedKeyEncryptionAlgorithms());
        allowedKeyAlgorithms.addAll(to.allowedKeyEncryptionAlgorithms());

        List<ContentEncryptionAlgorithm> allowedContentAlgorithms = new ArrayList<>();
        allowedContentAlgorithms.addAll(from.allowedContentEncryptionAlgorithms());
        allowedContentAlgorithms.addAll(to.allowedContentEncryptionAlgorithms());

        return EncryptionPolicy.builder()
                               .allowedKeyEncryptionAlgorithms(allowedKeyAlgorithms)
                               .allowedContentEncryptionAlgorithms(allowedContentAlgorithms)
                               .preferredKeyEncryptionAlgorithms(to.preferredKeyEncryptionAlgorithms())
                               .preferredContentEncryptionAlgorithm(to.preferredContentEncryptionAlgorithm())
                               .contentKeyGenerator(to.contentKeyGenerator())
                               .build();
    }

    interface Builder extends CopyableBuilder<Builder, EncryptionPolicy> {
        Builder allowedKeyEncryptionAlgorithms(ContentKeyEncryptionAlgorithm... allowedKeyEncryptionAlgorithms);
        Builder allowedKeyEncryptionAlgorithms(Collection<ContentKeyEncryptionAlgorithm> allowedKeyEncryptionAlgorithms);
        Builder addAllowedKeyEncryptionAlgorithm(ContentKeyEncryptionAlgorithm allowedKeyEncryptionAlgorithm);
        Builder clearAllowedKeyEncryptionAlgorithms();

        Builder preferredKeyEncryptionAlgorithms(ContentKeyEncryptionAlgorithm... preferredKeyEncryptionAlgorithms);
        Builder preferredKeyEncryptionAlgorithms(Collection<ContentKeyEncryptionAlgorithm> preferredKeyEncryptionAlgorithms);
        Builder addPreferredKeyEncryptionAlgorithm(ContentKeyEncryptionAlgorithm preferredKeyEncryptionAlgorithm);
        Builder clearPreferredKeyEncryptionAlgorithms();

        Builder allowedContentEncryptionAlgorithms(ContentEncryptionAlgorithm... readContentEncryptionAlgorithms);
        Builder allowedContentEncryptionAlgorithms(Collection<ContentEncryptionAlgorithm> readContentEncryptionAlgorithms);
        Builder addAllowedContentEncryptionAlgorithm(ContentEncryptionAlgorithm readContentEncryptionAlgorithm);
        Builder clearAllowedContentEncryptionAlgorithms();

        Builder preferredContentEncryptionAlgorithm(ContentEncryptionAlgorithm preferredContentEncryptionAlgorithm);

        Builder contentKeyGenerator(ContentKeyGenerator contentKeyGenerator);

        EncryptionPolicy build();

    }
}
