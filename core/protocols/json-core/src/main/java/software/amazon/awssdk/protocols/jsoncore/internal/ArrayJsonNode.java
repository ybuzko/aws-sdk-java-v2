package software.amazon.awssdk.protocols.jsoncore.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class ArrayJsonNode implements JsonNode {
    private final List<JsonNode> value;

    private ArrayJsonNode(List<JsonNode> value) {
        this.value = value;
    }

    public static ArrayJsonNode create(List<JsonNode> value) {
        Validate.notNull(value, "JSON array must not be null");
        Validate.noNullElements(value, "JSON array must not contain null");
        return new ArrayJsonNode(new ArrayList<>(value));
    }

    public static ArrayJsonNode createUnsafe(List<JsonNode> value) {
        return new ArrayJsonNode(value);
    }

    @Override
    public Type type() {
        return Type.ARRAY;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public List<JsonNode> asArray() {
        return Collections.unmodifiableList(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
