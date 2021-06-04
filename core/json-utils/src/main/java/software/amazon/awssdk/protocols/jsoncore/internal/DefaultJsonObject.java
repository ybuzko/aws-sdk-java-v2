package software.amazon.awssdk.protocols.jsoncore.internal;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonObject;
import software.amazon.awssdk.utils.Validate;

public class DefaultJsonObject implements JsonObject {
    private final Map<String, JsonNode> value;

    private DefaultJsonObject(Map<String, JsonNode> value) {
        this.value = value;
    }

    public static JsonObject create(Map<String, JsonNode> value) {
        Validate.notNull(value, "JSON object must not be null");
        Validate.noNullElements(value.keySet(), "JSON object keys must not contain null");
        Validate.noNullElements(value.values(), "JSON object values must not contain null");
        return new DefaultJsonObject(new LinkedHashMap<>(value));
    }

    public static JsonObject createUnsafe(Map<String, JsonNode> value) {
        return new DefaultJsonObject(value);
    }

    @Override
    public JsonNode get(String index) {
        return getOptional(index)
            .orElseThrow(() -> new IllegalStateException("JSON object does not contain index " + index));
    }

    @Override
    public Optional<JsonNode> getOptional(String index) {
        return Optional.ofNullable(value.get(index));
    }

    @Override
    public Map<String, JsonNode> asMap() {
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
