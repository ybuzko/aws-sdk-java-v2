package software.amazon.awssdk.protocols.jsoncore.internal;

import java.util.LinkedHashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class ObjectJsonNodeBuilder implements JsonNode.ObjectBuilder {
    private final Map<String, JsonNode> elements = new LinkedHashMap<>();

    public ObjectJsonNodeBuilder put(String key, JsonNode value) {
        Validate.notNull(value, "JSON object values must not be null.");
        elements.put(key, value);
        return this;
    }

    public JsonNode build() {
        return ObjectJsonNode.createUnsafe(elements);
    }
}
