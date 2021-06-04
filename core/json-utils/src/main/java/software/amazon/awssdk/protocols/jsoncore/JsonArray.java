package software.amazon.awssdk.protocols.jsoncore;

import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.protocols.jsoncore.internal.DefaultJsonArray;

public interface JsonArray {
    JsonNode get(int index);
    Optional<JsonNode> getOptional(int index);
    List<JsonNode> asList();

    static JsonArray create(List<JsonNode> number) {
        return DefaultJsonArray.create(number);
    }
}
