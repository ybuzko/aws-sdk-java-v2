package software.amazon.awssdk.protocols.jsoncore.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;

@SdkInternalApi
public final class NullJsonNode implements JsonNode {
    private static final NullJsonNode INSTANCE = new NullJsonNode();

    public static NullJsonNode instance() {
        return INSTANCE;
    }

    private NullJsonNode() {}

    @Override
    public Type type() {
        return Type.NULL;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String toString() {
        return "null";
    }
}
