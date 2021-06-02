package software.amazon.awssdk.protocols.json.dom;

public interface ArrayJsonNodeBuilder {
    ArrayJsonNodeBuilder add(JsonNode value);
    JsonNode build();
}
