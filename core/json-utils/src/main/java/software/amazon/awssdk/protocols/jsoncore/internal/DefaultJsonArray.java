package software.amazon.awssdk.protocols.jsoncore.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.protocols.jsoncore.JsonArray;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.utils.Validate;

public class DefaultJsonArray implements JsonArray {
    private final List<JsonNode> value;

    private DefaultJsonArray(List<JsonNode> value) {
        this.value = value;
    }

    public static JsonArray create(List<JsonNode> value) {
        Validate.notNull(value, "JSON array must not be null");
        Validate.noNullElements(value, "JSON array must not contain null");
        return new DefaultJsonArray(new ArrayList<>(value));
    }

    public static JsonArray createUnsafe(List<JsonNode> value) {
        return new DefaultJsonArray(value);
    }

    @Override
    public JsonNode get(int index) {
        return getOptional(index)
            .orElseThrow(() -> new IllegalStateException("JSON array does not contain index " + index));
    }

    @Override
    public Optional<JsonNode> getOptional(int index) {
        return Optional.ofNullable(value.get(index));
    }

    @Override
    public List<JsonNode> asList() {
        return Collections.unmodifiableList(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
