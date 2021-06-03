package software.amazon.awssdk.protocols.jsoncore.internal;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNumber;

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
    public JsonNumber asNumber() {
        throw new UnsupportedOperationException("A JSON boolean cannot be converted to a number.");
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("A JSON boolean cannot be converted to a string.");
    }

    @Override
    public boolean asBoolean() {
        return value;
    }

    @Override
    public List<JsonNode> asArray() {
        throw new UnsupportedOperationException("A JSON boolean cannot be converted to an array.");
    }

    @Override
    public Map<String, JsonNode> asObject() {
        throw new UnsupportedOperationException("A JSON boolean cannot be converted to an object.");
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
