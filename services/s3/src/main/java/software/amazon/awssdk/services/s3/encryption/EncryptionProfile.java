package software.amazon.awssdk.services.s3.encryption;

import java.util.function.Supplier;
import javax.crypto.KeyGenerator;

public interface EncryptionProfile {
//    EncryptionProfile LEGACY =
//        builder().keyWrapReadAlgorithms(KeyWrapAlgorithm.values())
//                 .keyWrapWriteAlgorithm(KeyWrapAlgorithm.AES_GCM)
//                 .contentReadAlgorithms(ContentEncryptionAlgorithm.values())
//                 .contentWriteAlgorithm(ContentEncryptionAlgorithm.AES_GCM)
//                 .build();
//
//    EncryptionProfile V2021_02_15 =
//        builder().keyWrapReadAlgorithms(KeyWrapAlgorithm.KMS, KeyWrapAlgorithm.AES_GCM, KeyWrapAlgorithm.RSA_OAEP_SHA1)
//                 .keyWrapWriteAlgorithm(KeyWrapAlgorithm.AES_GCM)
//                 .contentReadAlgorithms(ContentEncryptionAlgorithm.AES_GCM)
//                 .contentWriteAlgorithm(ContentEncryptionAlgorithm.AES_GCM)
//                 .build();
//
//
//    interface Builder {
//        Builder keyWrapReadAlgorithms(KeyWrapAlgorithm... algorithms);
//        Builder keyWrapWriteAlgorithm(KeyWrapAlgorithm algorithm);
//        Builder contentReadAlgorithms(ContentEncryptionAlgorithm... algorithms);
//        Builder contentWriteAlgorithm(ContentEncryptionAlgorithm algorithms);
//        EncryptionProfile build();
//    }

    static Builder builder() {
        return null;
    }

    static EncryptionProfile legacy() {
        return builder().keyGenerator(ContentKeyGenerator.AES_256)
                        .keyEncryptor(ContentKeyEncryptor.)
                        .build();
    }

    static EncryptionProfile v1() {
        return builder()
    }

    interface Builder {
        Builder keyGenerator(ContentKeyGenerator keyGenerator);
        Builder keyEncryptor(ContentKeyEncryptor contentKeyEncryptor);
        Builder contentEncryptor(ContentEncryptor contentEncryptor);
        EncryptionProfile build();
    }

    static final KeyGenerator createAesKeyGenerator() {

    }
}
