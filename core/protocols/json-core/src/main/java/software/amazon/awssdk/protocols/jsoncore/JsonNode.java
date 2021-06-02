package software.amazon.awssdk.protocols.jsoncore;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.protocols.jsoncore.internal.ArrayJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.ArrayJsonNodeBuilder;
import software.amazon.awssdk.protocols.jsoncore.internal.BooleanJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.JsonNodeParser;
import software.amazon.awssdk.protocols.jsoncore.internal.NullJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.NumberJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.ObjectJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.ObjectJsonNodeBuilder;
import software.amazon.awssdk.protocols.jsoncore.internal.StringJsonNode;
import software.amazon.awssdk.thirdparty.jackson.core.JsonFactory;

@SdkProtectedApi
public interface JsonNode {
    Type type();

    default boolean isNumber() {
        return false;
    }

    default boolean isString() {
        return false;
    }

    default boolean isBoolean() {
        return false;
    }

    default boolean isNull() {
        return false;
    }

    default boolean isArray() {
        return false;
    }

    default boolean isObject() {
        return false;
    }

    default JsonNumber asNumber() {
        throw new UnsupportedOperationException();
    }

    default String asString() {
        throw new UnsupportedOperationException();
    }

    default boolean asBoolean() {
        throw new UnsupportedOperationException();
    }

    default List<JsonNode> asArray() {
        throw new UnsupportedOperationException();
    }

    default Map<String, JsonNode> asObject() {
        throw new UnsupportedOperationException();
    }

    static JsonNode nullNode() {
        return NullJsonNode.instance();
    }

    static JsonNode stringNode(String string) {
        return new StringJsonNode(string);
    }

    static JsonNode booleanNode(boolean bool) {
        return new BooleanJsonNode(bool);
    }

    static JsonNode numberNode(JsonNumber number) {
        return new NumberJsonNode(number);
    }

    static JsonNode arrayNode(List<JsonNode> array) {
        return ArrayJsonNode.create(array);
    }

    static JsonNode objectNode(Map<String, JsonNode> object) {
        return ObjectJsonNode.create(object);
    }

    static JsonNode parse(JsonFactory factory, InputStream input) {
        return JsonNodeParser.parse(factory, input);
    }

    static ObjectBuilder objectNodeBuilder() {
        return new ObjectJsonNodeBuilder();
    }

    static ArrayBuilder arrayNodeBuilder() {
        return new ArrayJsonNodeBuilder();
    }

    enum Type {
        NULL,
        STRING,
        BOOLEAN,
        NUMBER,
        ARRAY,
        OBJECT
    }

    interface ArrayBuilder {
        ArrayBuilder add(JsonNode value);
        JsonNode build();
    }

    interface ObjectBuilder {
        ObjectBuilder put(String key, JsonNode value);
        JsonNode build();
    }
}
