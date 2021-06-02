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

package software.amazon.awssdk.protocols.jsoncore.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNumber;
import software.amazon.awssdk.thirdparty.jacksoncore.core.JsonFactory;
import software.amazon.awssdk.thirdparty.jacksoncore.core.JsonParser;
import software.amazon.awssdk.thirdparty.jacksoncore.core.JsonToken;

/**
 * Parses an JSON document into a simple DOM-like structure, {@link JsonNode}.
 */
@SdkInternalApi
public final class JsonNodeParser {
    public static JsonNode parse(JsonFactory factory, InputStream content) {
        try (JsonParser parser = factory.createParser(content)
                                        .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false)) {
            return parseToken(parser, parser.nextToken());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static JsonNode parseToken(JsonParser parser, JsonToken token) throws IOException {
        if (token == null) {
            return null;
        }
        switch (token) {
            case VALUE_STRING:
                return JsonNode.stringNode(parser.getText());
            case VALUE_FALSE:
                return JsonNode.booleanNode(false);
            case VALUE_TRUE:
                return JsonNode.booleanNode(true);
            case VALUE_NULL:
                return JsonNode.nullNode();
            case VALUE_NUMBER_FLOAT:
            case VALUE_NUMBER_INT:
                return JsonNode.numberNode(JsonNumber.of(parser.getNumberValue()));
            case START_OBJECT:
                return parseObject(parser);
            case START_ARRAY:
                return parseArray(parser);
            default:
                throw new IllegalArgumentException("Unexpected JSON token - " + token);
        }
    }

    private static JsonNode parseObject(JsonParser parser) throws IOException {
        JsonToken currentToken = parser.nextToken();
        JsonNode.ObjectBuilder builder = JsonNode.objectNodeBuilder();
        while (currentToken != JsonToken.END_OBJECT) {
            String fieldName = parser.getText();
            builder.put(fieldName, parseToken(parser, parser.nextToken()));
            currentToken = parser.nextToken();
        }
        return builder.build();
    }

    private static JsonNode parseArray(JsonParser parser) throws IOException {
        JsonToken currentToken = parser.nextToken();
        JsonNode.ArrayBuilder builder = JsonNode.arrayNodeBuilder();
        while (currentToken != JsonToken.END_ARRAY) {
            builder.add(parseToken(parser, currentToken));
            currentToken = parser.nextToken();
        }
        return builder.build();
    }
}
