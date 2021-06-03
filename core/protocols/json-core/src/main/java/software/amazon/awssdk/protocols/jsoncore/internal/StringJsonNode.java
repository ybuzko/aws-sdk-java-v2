package software.amazon.awssdk.protocols.jsoncore.internal;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNumber;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class StringJsonNode implements JsonNode {
    private final String value;

    public StringJsonNode(String value) {
        this.value = Validate.notNull(value, "JSON string must not be null");
    }

    @Override
    public Type type() {
        return Type.STRING;
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public JsonNumber asNumber() {
        throw new UnsupportedOperationException("A JSON string cannot be converted to a number.");
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public boolean asBoolean() {
        throw new UnsupportedOperationException("A JSON string cannot be converted to a boolean.");
    }

    @Override
    public List<JsonNode> asArray() {
        throw new UnsupportedOperationException("A JSON string cannot be converted to an array.");
    }

    @Override
    public Map<String, JsonNode> asObject() {
        throw new UnsupportedOperationException("A JSON string cannot be converted to an object.");
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
