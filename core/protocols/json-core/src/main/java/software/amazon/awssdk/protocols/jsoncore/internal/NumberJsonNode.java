package software.amazon.awssdk.protocols.jsoncore.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNumber;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class NumberJsonNode implements JsonNode {
    private final JsonNumber value;

    public NumberJsonNode(JsonNumber value) {
        this.value = Validate.notNull(value, "JSON number must not be null");
    }

    @Override
    public Type type() {
        return Type.NUMBER;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public JsonNumber asNumber() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
