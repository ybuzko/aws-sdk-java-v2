package software.amazon.awssdk.services.s3.encryption;

import java.util.HashMap;
import java.util.Map;

public final class ContentEncryptionAlgorithm {
    public static final ContentEncryptionAlgorithm AES_GCM = ContentEncryptionAlgorithm.of("AES/GCM/NoPadding");
    public static final ContentEncryptionAlgorithm AES_CBC = ContentEncryptionAlgorithm.of("AES/CBC/PKCS5Padding");

    @Deprecated
    public static final ContentEncryptionAlgorithm AES_CTR = ContentEncryptionAlgorithm.of("AES/CTR/NoPadding");

    private static Map<String, ContentEncryptionAlgorithm> ALL_ALGORITHMS;

    private final String name;

    private ContentEncryptionAlgorithm(String name) {
        this.name = name;
    }

    public static ContentEncryptionAlgorithm of(String name) {
        synchronized(ContentEncryptionAlgorithm.class) {
            if (ALL_ALGORITHMS == null) {
                ALL_ALGORITHMS = new HashMap<>();
            }
            return ALL_ALGORITHMS.computeIfAbsent(name, ContentEncryptionAlgorithm::new);
        }
    }

    public String name() {
        return name;
    }
}
