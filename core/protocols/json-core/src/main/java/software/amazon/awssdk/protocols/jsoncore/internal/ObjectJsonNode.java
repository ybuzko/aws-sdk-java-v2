package software.amazon.awssdk.protocols.jsoncore.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class ObjectJsonNode implements JsonNode {
    private final Map<String, JsonNode> value;

    private ObjectJsonNode(Map<String, JsonNode> value) {
        this.value = value;
    }

    public static ObjectJsonNode create(Map<String, JsonNode> value) {
        Validate.notNull(value, "JSON object must not be null");
        Validate.noNullElements(value.keySet(), "JSON object keys must not contain null");
        Validate.noNullElements(value.values(), "JSON object values must not contain null");
        return new ObjectJsonNode(new HashMap<>(value));
    }

    public static ObjectJsonNode createUnsafe(Map<String, JsonNode> value) {
        return new ObjectJsonNode(value);
    }

    @Override
    public Type type() {
        return Type.OBJECT;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public Map<String, JsonNode> asObject() {
        return Collections.unmodifiableMap(value);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("{");
        value.forEach((k, v) -> output.append("\"").append(k).append("\"")
                                      .append(": ").append(v).append(","));
        output.setLength(output.length() - 1); // Remove trailing comma
        output.append("}");
        return output.toString();
    }
}
