/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.protocols.jsoncore;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.protocols.jsoncore.internal.ArrayJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.ArrayJsonNodeBuilder;
import software.amazon.awssdk.protocols.jsoncore.internal.BooleanJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.EmbeddedObjectJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.NullJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.NumberJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.ObjectJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.ObjectJsonNodeBuilder;
import software.amazon.awssdk.protocols.jsoncore.internal.StringJsonNode;

/**
 * A node in a JSON document. Either a number, string, boolean, array, object or null. Also can be an embedded object,
 * which is a non-standard type used in JSON extensions, like CBOR.
 *
 * <p>Created from a JSON document with {@link JsonNodeParser} or directly with static methods like
 * {@link #numberNode(JsonNumber)} or {@link #stringNode(String)}.
 *
 * <p>The type of node can be determined using {@link #type()} or "is" methods like {@link #isNumber()} and
 * {@link #isString()}</p>. Once the type is determined, the value of the node can be extracted via the "as" methods, like
 * {@link #asNumber()} and {@link #asString()}.
 */
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

    default boolean isEmbeddedObject() {
        return false;
    }

    JsonNumber asNumber();

    String asString();

    boolean asBoolean();

    JsonArray asArray();

    JsonObject asObject();

    Object asEmbeddedObject();

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

    static JsonNode arrayNode(JsonArray array) {
        return new ArrayJsonNode(array);
    }

    static JsonNode objectNode(JsonObject object) {
        return new ObjectJsonNode(object);
    }

    static JsonNode embeddedObjectNode(Object embeddedObject) {
        return new EmbeddedObjectJsonNode(embeddedObject);
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
        OBJECT,
        EMBEDDED_OBJECT
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
