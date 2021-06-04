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

import java.util.ArrayList;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonArray;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class ArrayJsonNodeBuilder implements JsonNode.ArrayBuilder {
    private final List<JsonNode> nodes = new ArrayList<>();

    public ArrayJsonNodeBuilder add(JsonNode value) {
        Validate.notNull(value, "JSON array entry must not be null");
        nodes.add(value);
        return this;
    }

    public JsonNode build() {
        return new ArrayJsonNode(JsonArray.create(nodes));
    }
}
