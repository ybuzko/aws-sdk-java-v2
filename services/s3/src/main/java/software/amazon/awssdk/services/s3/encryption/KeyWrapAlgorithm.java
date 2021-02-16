package software.amazon.awssdk.services.s3.encryption;

public final class KeyWrapAlgorithm {
    public static final KeyWrapAlgorithm RSA_OAEP_SHA1 =
        KeyWrapAlgorithm.create("RSA-OAEP-SHA1", ContentKeyEncryptor.RSA_OAEP_SHA1);

    public static final KeyWrapAlgorithm AES_GCM =
        KeyWrapAlgorithm.create("AES/GCM");

    public static final KeyWrapAlgorithm KMS =
        KeyWrapAlgorithm.create("kms+context");

    @Deprecated
    public static final KeyWrapAlgorithm KMS_NO_CONTEXT =
        KeyWrapAlgorithm.create("kms");

    @Deprecated
    public static final KeyWrapAlgorithm AES =
        KeyWrapAlgorithm.create("AESWrap");

    @Deprecated
    public static final KeyWrapAlgorithm RSA_ECB_OAEP_SHA256_MGF1_PADDING =
        KeyWrapAlgorithm.create("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

    private final String name;
    private final ContentKeyEncryptor encryptor;

    private KeyWrapAlgorithm(String name, ContentKeyEncryptor encryptor) {
        this.name = name;
        this.encryptor = encryptor;
    }

    public static KeyWrapAlgorithm create(String name, ContentKeyEncryptor encryptor) {
        return new KeyWrapAlgorithm(name, encryptor);
    }

    public String name() {
        return name;
    }

    public ContentKeyEncryptor encryptor() {
        return encryptor;
    }
}
