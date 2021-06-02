package software.amazon.awssdk.protocols.jsoncore.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;

@SdkInternalApi
public final class BooleanJsonNode implements JsonNode {
    private final boolean value;

    public BooleanJsonNode(boolean value) {
        this.value = value;
    }

    @Override
    public Type type() {
        return Type.BOOLEAN;
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    @Override
    public boolean asBoolean() {
        return value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
