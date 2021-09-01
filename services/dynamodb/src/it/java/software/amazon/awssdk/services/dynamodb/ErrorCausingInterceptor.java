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

package software.amazon.awssdk.services.dynamodb;

import java.util.concurrent.atomic.AtomicInteger;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.http.SdkHttpResponse;

public class ErrorCausingInterceptor implements ExecutionInterceptor {
    private final AtomicInteger attempt = new AtomicInteger(0);
    private final int failures;

    public ErrorCausingInterceptor(int failures) {
        this.failures = failures;
    }

    @Override
    public SdkHttpResponse modifyHttpResponse(Context.ModifyHttpResponse context, ExecutionAttributes executionAttributes) {
        int attempt = this.attempt.incrementAndGet();
        if (attempt <= failures) {
            return context.httpResponse()
                          .toBuilder()
                          .statusCode(500)
                          .build();
        }

        return context.httpResponse();
    }
}
