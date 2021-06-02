package software.amazon.awssdk.protocols.jsoncore.internal;

import java.util.ArrayList;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class ArrayJsonNodeBuilder implements JsonNode.ArrayBuilder {
    private final List<JsonNode> nodes = new ArrayList<>();

    public ArrayJsonNodeBuilder add(JsonNode value) {
        Validate.notNull(value, "JSON array entry must not be null");
        nodes.add(value);
        return this;
    }

    public JsonNode build() {
        return ArrayJsonNode.createUnsafe(nodes);
    }
}
