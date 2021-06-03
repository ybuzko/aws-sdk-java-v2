package software.amazon.awssdk.protocols.jsoncore.internal;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNumber;

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
    public JsonNumber asNumber() {
        throw new UnsupportedOperationException("A JSON null cannot be converted to a number.");
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("A JSON null cannot be converted to a string.");
    }

    @Override
    public boolean asBoolean() {
        throw new UnsupportedOperationException("A JSON null cannot be converted to a boolean.");
    }

    @Override
    public List<JsonNode> asArray() {
        throw new UnsupportedOperationException("A JSON null cannot be converted to an array.");
    }

    @Override
    public Map<String, JsonNode> asObject() {
        throw new UnsupportedOperationException("A JSON null cannot be converted to an object.");
    }

    @Override
    public String toString() {
        return "null";
    }
}
