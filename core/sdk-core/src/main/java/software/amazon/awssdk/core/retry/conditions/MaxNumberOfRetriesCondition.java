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

package software.amazon.awssdk.core.retry.conditions;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.internal.retry.SdkDefaultRetrySetting;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.executionlog.ExecutionLogType;

/**
 * Simple retry condition that allows retries up to a certain max number of retries.
 */
@SdkPublicApi
public final class MaxNumberOfRetriesCondition implements RetryCondition {

    private final int maxNumberOfRetries;

    private MaxNumberOfRetriesCondition(int maxNumberOfRetries) {
        this.maxNumberOfRetries = Validate.isNotNegative(maxNumberOfRetries, "maxNumberOfRetries");
    }

    public static MaxNumberOfRetriesCondition create(int maxNumberOfRetries) {
        return new MaxNumberOfRetriesCondition(maxNumberOfRetries);
    }

    public static MaxNumberOfRetriesCondition forRetryMode(RetryMode retryMode) {
        return create(SdkDefaultRetrySetting.maxAttempts(retryMode));
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        boolean shouldRetry = context.retriesAttempted() < maxNumberOfRetries;
        if (!shouldRetry) {
            context.executionAttributes().getAttribute(SdkExecutionAttribute.EXECUTION_LOG)
                   .add(ExecutionLogType.RETRY, () -> "Maximum number of retry attempts has been reached.");
        }
        return shouldRetry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MaxNumberOfRetriesCondition that = (MaxNumberOfRetriesCondition) o;

        return maxNumberOfRetries == that.maxNumberOfRetries;
    }

    @Override
    public int hashCode() {
        return maxNumberOfRetries;
    }

    @Override
    public String toString() {
        return ToString.builder("MaxNumberOfRetriesCondition")
                       .add("maxNumberOfRetries", maxNumberOfRetries)
                       .build();
    }
}
