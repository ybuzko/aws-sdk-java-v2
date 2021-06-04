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

import java.util.Collections;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.protocols.jsoncore.internal.ArrayJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.BooleanJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.EmbeddedObjectJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.NullJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.NumberJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.ObjectJsonNode;
import software.amazon.awssdk.protocols.jsoncore.internal.StringJsonNode;
import software.amazon.awssdk.thirdparty.jackson.core.JsonFactory;

/**
 * A node in a JSON document. Either a number, string, boolean, array, object or null. Also can be an embedded object,
 * which is a non-standard type used in JSON extensions, like CBOR.
 *
 * <p>Created from a JSON document with {@link JsonNodeParser} or directly with static methods like
 * {@link #numberNode(JsonNumber)} or {@link #stringNode(String)}.
 *
 * <p>The type of node can be determined using "is" methods like {@link #isNumber()} and {@link #isString()}.
 * Once the type is determined, the value of the node can be extracted via the "as" methods, like {@link #asNumber()}
 * and {@link #asString()}.
 */
@SdkProtectedApi
public interface JsonNode {
    /**
     * Create a node that represents a JSON null: https://datatracker.ietf.org/doc/html/rfc8259#section-3
     */
    static JsonNode nullNode() {
        return NullJsonNode.instance();
    }

    /**
     * Create a node that represents a JSON string: https://datatracker.ietf.org/doc/html/rfc8259#section-7
     */
    static JsonNode stringNode(String string) {
        return new StringJsonNode(string);
    }

    /**
     * Create a node that represents a JSON boolean: https://datatracker.ietf.org/doc/html/rfc8259#section-3
     */
    static JsonNode booleanNode(boolean bool) {
        return new BooleanJsonNode(bool);
    }

    /**
     * Create a node that represents a JSON number: https://datatracker.ietf.org/doc/html/rfc8259#section-6
     */
    static JsonNode numberNode(JsonNumber number) {
        return new NumberJsonNode(number);
    }

    /**
     * Create a node that represents a JSON array: https://datatracker.ietf.org/doc/html/rfc8259#section-5
     */
    static JsonNode arrayNode(JsonArray array) {
        return new ArrayJsonNode(array);
    }

    /**
     * A convenience for invoking {@link #arrayNode(JsonArray)} with an empty list.
     */
    static JsonNode emptyArrayNode() {
        return new ArrayJsonNode(JsonArray.create(Collections.emptyList()));
    }

    /**
     * Create a node that represents a JSON object: https://datatracker.ietf.org/doc/html/rfc8259#section-4
     */
    static JsonNode objectNode(JsonObject object) {
        return new ObjectJsonNode(object);
    }

    /**
     * A convenience for invoking {@link #objectNode(JsonObject)} with an empty object.
     */
    static JsonNode emptyObjectNode() {
        return new ObjectJsonNode(JsonObject.create(Collections.emptyMap()));
    }

    /**
     * Create a node that represents a JSON embedded object.
     *
     * @see #isEmbeddedObject()
     */
    static JsonNode embeddedObjectNode(Object embeddedObject) {
        return new EmbeddedObjectJsonNode(embeddedObject);
    }

    /**
     * Returns true if this node represents a JSON number: https://datatracker.ietf.org/doc/html/rfc8259#section-6
     *
     * @see #asNumber()
     */
    default boolean isNumber() {
        return false;
    }

    /**
     * Returns true if this node represents a JSON string: https://datatracker.ietf.org/doc/html/rfc8259#section-7
     *
     * @see #asString()
     */
    default boolean isString() {
        return false;
    }

    /**
     * Returns true if this node represents a JSON boolean: https://datatracker.ietf.org/doc/html/rfc8259#section-3
     *
     * @see #asBoolean()
     */
    default boolean isBoolean() {
        return false;
    }

    /**
     * Returns true if this node represents a JSON null: https://datatracker.ietf.org/doc/html/rfc8259#section-3
     */
    default boolean isNull() {
        return false;
    }

    /**
     * Returns true if this node represents a JSON array: https://datatracker.ietf.org/doc/html/rfc8259#section-5
     *
     * @see #asArray()
     */
    default boolean isArray() {
        return false;
    }

    /**
     * Returns true if this node represents a JSON object: https://datatracker.ietf.org/doc/html/rfc8259#section-4
     *
     * @see #asObject()
     */
    default boolean isObject() {
        return false;
    }

    /**
     * Returns true if this node represents a JSON "embedded object". This non-standard type is associated with JSON extensions,
     * like CBOR or ION. It allows additional data types to be embedded in a JSON document, like a timestamp or a raw byte array.
     *
     * <p>Users who are only concerned with handling JSON can ignore this field. It will only be present when using a custom
     * {@link JsonFactory} via {@link JsonNodeParser.Builder#jsonFactory(JsonFactory)}, or if this was created via
     * {@link #embeddedObjectNode(Object)}.
     *
     * @see #asEmbeddedObject()
     */
    default boolean isEmbeddedObject() {
        return false;
    }

    /**
     * When {@link #isNumber()} is true, this returns the number associated with this node. This will throw an exception if
     * {@link #isNumber()} is false.
     */
    JsonNumber asNumber();

    /**
     * When {@link #isString()}, {@link #isNumber()}, or {@link #isBoolean()} is true, this returns the string associated
     * with this node. In the case of numbers and booleans, this is the string representation of those values.
     *
     * <p>This will throw an exception if the value cannot be meaningfully converted to a string is false.
     */
    String asString();

    /**
     * When {@link #isBoolean()} is true, this returns the boolean associated with this node. This will throw an exception if
     * {@link #isBoolean()} is false.
     */
    boolean asBoolean();

    /**
     * When {@link #isArray()} is true, this returns the array associated with this node. This will throw an exception if
     * {@link #isArray()} is false.
     */
    JsonArray asArray();

    /**
     * When {@link #isObject()} is true, this returns the object associated with this node. This will throw an exception if
     * {@link #isObject()} is false.
     */
    JsonObject asObject();

    /**
     * When {@link #isEmbeddedObject()} is true, this returns the embedded object associated with this node. This will throw
     * an exception if {@link #isEmbeddedObject()} is false.
     *
     * @see #isEmbeddedObject()
     */
    Object asEmbeddedObject();
}
