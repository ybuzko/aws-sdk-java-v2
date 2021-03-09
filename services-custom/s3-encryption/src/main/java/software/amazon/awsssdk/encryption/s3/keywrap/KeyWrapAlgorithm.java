package software.amazon.awsssdk.encryption.s3.keywrap;

public final class KeyWrapAlgorithm {
    public static final KeyWrapAlgorithm RSA_OAEP_SHA1 = of("RSA-OAEP-SHA1");

    public static final KeyWrapAlgorithm AES_GCM = of("AES/GCM");

    public static final KeyWrapAlgorithm KMS = of("kms+context");

    @Deprecated
    public static final KeyWrapAlgorithm KMS_NO_CONTEXT = of("kms");

    @Deprecated
    public static final KeyWrapAlgorithm AES = of("AESWrap");

    @Deprecated
    public static final KeyWrapAlgorithm RSA_ECB_OAEP_SHA256_MGF1_PADDING = of("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

    private final String name;

    private KeyWrapAlgorithm(String name) {
        this.name = name;
    }

    public static KeyWrapAlgorithm of(String name) {
        return new KeyWrapAlgorithm(name);
    }

    public String name() {
        return name;
    }
}
