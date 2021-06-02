package software.amazon.awssdk.protocols.json.dom;

public interface ObjectJsonNodeBuilder {
    ObjectJsonNodeBuilder put(String key, JsonNode value);
    JsonNode build();
}
