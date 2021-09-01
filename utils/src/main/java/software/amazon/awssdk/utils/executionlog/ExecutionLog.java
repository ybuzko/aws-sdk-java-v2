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

package software.amazon.awssdk.utils.executionlog;

import java.util.function.Supplier;
import software.amazon.awssdk.utils.internal.executionlog.DefaultExecutionLog;
import software.amazon.awssdk.utils.internal.executionlog.NoopExecutionLog;

public interface ExecutionLog {
    static ExecutionLog create(ExecutionLogType... enabledLogTypes) {
        return new DefaultExecutionLog(enabledLogTypes);
    }

    static ExecutionLog disabled() {
        return new NoopExecutionLog();
    }

    String serviceName();

    String operationName();

    String toString();

    void serviceName(String serviceName);

    void operationName(String operationName);

    void add(ExecutionLogType logType, Supplier<String> msg);

    void add(ExecutionLogType logType, Supplier<String> msg, Throwable throwable);
}
