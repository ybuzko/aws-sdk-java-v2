package software.amazon.awssdk.protocols.json.dom;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;

@SdkPublicApi
public interface JsonNode {
    Type type();

    boolean isNumber();
    boolean isString();
    boolean isBoolean();
    boolean isNull();
    boolean isArray();
    boolean isObject();

    JsonNumber asNumber();
    String asString();
    boolean asBoolean();
    List<JsonNode> asArray();
    Map<String, JsonNode> asObject();

    JsonNode get(String... path);

    JsonNode nullNode();
    JsonNode stringNode(String string);
    JsonNode booleanNode(Boolean bool);
    JsonNode numberNode(JsonNumber number);
    JsonNode arrayNode(List<JsonNode> array);
    JsonNode objectNode(Map<String, JsonNode> object);

    JsonNode parse(InputStream input);
    JsonNode parse(String input);
    JsonNode parse(byte[] input);

    ObjectJsonNodeBuilder objectNodeBuilder();
    ArrayJsonNodeBuilder arrayNodeBuilder();

    enum Type {
        NULL,
        STRING,
        BOOLEAN,
        NUMBER,
        ARRAY,
        OBJECT
    }
}
