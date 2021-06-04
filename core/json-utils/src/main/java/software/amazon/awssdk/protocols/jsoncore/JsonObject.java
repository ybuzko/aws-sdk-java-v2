package software.amazon.awssdk.protocols.jsoncore;

import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.protocols.jsoncore.internal.DefaultJsonObject;

public interface JsonObject {
    JsonNode get(String key);
    Optional<JsonNode> getOptional(String key);
    Map<String, JsonNode> asMap();

    static JsonObject create(Map<String, JsonNode> number) {
        return DefaultJsonObject.create(number);
    }
}
