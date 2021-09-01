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

package software.amazon.awssdk.core.internal.interceptor;

import static software.amazon.awssdk.core.http.HttpResponseHandler.X_AMZN_REQUEST_ID_HEADERS;
import static software.amazon.awssdk.core.http.HttpResponseHandler.X_AMZ_ID_2_HEADER;
import static software.amazon.awssdk.utils.executionlog.ExecutionLogType.CLIENT_INPUT_OUTPUT;
import static software.amazon.awssdk.utils.executionlog.ExecutionLogType.IDS;

import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.utils.executionlog.ExecutionLog;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

public class ExecutionLogInterceptor implements ExecutionInterceptor {
    @Override
    public void beforeExecution(Context.BeforeExecution context, ExecutionAttributes executionAttributes) {
        executionLog(executionAttributes).add(CLIENT_INPUT_OUTPUT, () -> "SDK Request: " + context.request());
    }

    @Override
    public void afterTransmission(Context.AfterTransmission context, ExecutionAttributes executionAttributes) {
        executionLog(executionAttributes)
            .add(IDS, () -> "Request ID: " + requestId(context) + ", Extended Request ID: " + extendedRequestId(context));
    }

    @Override
    public void afterUnmarshalling(Context.AfterUnmarshalling context, ExecutionAttributes executionAttributes) {
        executionLog(executionAttributes).add(CLIENT_INPUT_OUTPUT, () -> "SDK Response: " + context.response());
    }

    @Override
    public void onExecutionFailure(Context.FailedExecution context, ExecutionAttributes executionAttributes) {
        executionLog(executionAttributes).add(CLIENT_INPUT_OUTPUT, () -> "SDK Exception: " + context.exception());
    }

    private String requestId(Context.AfterTransmission context) {
        return SdkHttpUtils.firstMatchingHeaderFromCollection(context.httpResponse().headers(), X_AMZN_REQUEST_ID_HEADERS)
                           .orElse(null);
    }

    private String extendedRequestId(Context.AfterTransmission context) {
        return context.httpResponse().firstMatchingHeader(X_AMZ_ID_2_HEADER).orElse(null);
    }

    private ExecutionLog executionLog(ExecutionAttributes attributes) {
        return attributes.getAttribute(SdkExecutionAttribute.EXECUTION_LOG);
    }
}
