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

import static software.amazon.awssdk.utils.FunctionalUtils.invokeSafely;

import java.io.IOException;
import java.io.InputStream;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.thirdparty.jackson.core.JsonFactory;
import software.amazon.awssdk.thirdparty.jackson.core.JsonParseException;
import software.amazon.awssdk.thirdparty.jackson.core.JsonParser;
import software.amazon.awssdk.thirdparty.jackson.core.JsonToken;
import software.amazon.awssdk.thirdparty.jackson.core.StreamReadFeature;
import software.amazon.awssdk.thirdparty.jackson.core.json.JsonReadFeature;

/**
 * Parses an JSON document into a simple DOM-like structure, {@link JsonNode}.
 */
@SdkProtectedApi
public final class JsonNodeParser {
    public static final JsonFactory DEFAULT_JSON_FACTORY =
        JsonFactory.builder()
                   .configure(JsonReadFeature.ALLOW_JAVA_COMMENTS, true)
                   .configure(StreamReadFeature.AUTO_CLOSE_SOURCE, false)
                   .build();

    private final boolean removeErrorLocations;
    private final JsonFactory jsonFactory;

    private JsonNodeParser(Builder builder) {
        this.removeErrorLocations = builder.removeErrorLocations;
        this.jsonFactory = builder.jsonFactory;
    }

    public static JsonNodeParser create() {
        return builder().build();
    }

    public static JsonNodeParser.Builder builder() {
        return new Builder();
    }

    public JsonNode parse(InputStream content) {
        return invokeSafely(() -> {
            try (JsonParser parser = jsonFactory.createParser(content)
                                                .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false)) {
                return parse(parser);
            }
        });
    }

    public JsonNode parse(String content) {
        return invokeSafely(() -> {
            try (JsonParser parser = jsonFactory.createParser(content)
                                                .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false)) {
                return parse(parser);
            }
        });
    }

    private JsonNode parse(JsonParser parser) throws IOException {
        try {
            return parseToken(parser, parser.nextToken());
        } catch (Exception e) {
            removeErrorLocationsIfRequired(e);
            throw e;
        }
    }

    private void removeErrorLocationsIfRequired(Throwable exception) {
        if (removeErrorLocations) {
            removeErrorLocations(exception);
        }
    }

    private void removeErrorLocations(Throwable exception) {
        if (exception == null) {
            return;
        }

        if (exception instanceof JsonParseException) {
            ((JsonParseException) exception).clearLocation();
        }

        removeErrorLocations(exception.getCause());
    }

    private JsonNode parseToken(JsonParser parser, JsonToken token) throws IOException {
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

    private JsonNode parseObject(JsonParser parser) throws IOException {
        JsonToken currentToken = parser.nextToken();
        JsonNode.ObjectBuilder builder = JsonNode.objectNodeBuilder();
        while (currentToken != JsonToken.END_OBJECT) {
            String fieldName = parser.getText();
            builder.put(fieldName, parseToken(parser, parser.nextToken()));
            currentToken = parser.nextToken();
        }
        return builder.build();
    }

    private JsonNode parseArray(JsonParser parser) throws IOException {
        JsonToken currentToken = parser.nextToken();
        JsonNode.ArrayBuilder builder = JsonNode.arrayNodeBuilder();
        while (currentToken != JsonToken.END_ARRAY) {
            builder.add(parseToken(parser, currentToken));
            currentToken = parser.nextToken();
        }
        return builder.build();
    }

    public static final class Builder {
        private JsonFactory jsonFactory = DEFAULT_JSON_FACTORY;
        private boolean removeErrorLocations = false;

        public Builder removeErrorLocations(boolean removeErrorLocations) {
            this.removeErrorLocations = removeErrorLocations;
            return this;
        }

        /**
         * TODO: Recommended to share JsonFactory instances per http://wiki.fasterxml
         * .com/JacksonBestPracticesPerformance
         */
        public Builder jsonFactory(JsonFactory jsonFactory) {
            this.jsonFactory = jsonFactory;
            return this;
        }

        public JsonNodeParser build() {
            return new JsonNodeParser(this);
        }
    }
}
