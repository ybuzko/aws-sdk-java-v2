package software.amazon.awssdk.protocols.jsoncore.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class StringJsonNode implements JsonNode {
    private final String value;

    public StringJsonNode(String value) {
        this.value = Validate.notNull(value, "JSON string must not be null");
    }

    @Override
    public Type type() {
        return Type.STRING;
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
