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

import java.util.concurrent.CompletionException;
import org.junit.Test;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.utils.executionlog.ExecutionLog;
import software.amazon.awssdk.utils.executionlog.ExecutionLogType;

public class ExecutionLogDemo {
    @Test
    public void demo() {
        try (DynamoDbAsyncClient client = errorCausingClient()) {
            ListTablesResponse response =
                client.listTables(ListTablesRequest.builder()
                                                   .overrideConfiguration(c -> c.enableExecutionLogging(ExecutionLogType.WIRE))
                                                   .build())
                      .join();

            ExecutionLog executionLog = response.sdkExecutionLog();
            System.out.println(executionLog);
        } catch (CompletionException e) {
            if (e.getCause() instanceof SdkException) {
                SdkException cause = (SdkException) e.getCause();
                System.out.println(cause.sdkExecutionLog());
            }
        }
    }












    private DynamoDbAsyncClient errorCausingClient() {
        return DynamoDbAsyncClient.builder()
                                  .overrideConfiguration(c -> c.retryPolicy(RetryMode.STANDARD)
                                                               .addExecutionInterceptor(new ErrorCausingInterceptor(3)))
                                  .build();
    }
}
