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

package software.amazon.awssdk.utils.internal.executionlog;

import static java.util.Arrays.asList;
import static java.util.Collections.synchronizedList;
import static java.util.Collections.unmodifiableSet;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import software.amazon.awssdk.utils.executionlog.ExecutionLog;
import software.amazon.awssdk.utils.executionlog.ExecutionLogType;

public final class DefaultExecutionLog implements ExecutionLog {
    private final List<ExecutionLogEntry> logs;
    private final Set<ExecutionLogType> enabledLogTypes;
    private String serviceName;
    private String operationName;

    public DefaultExecutionLog(ExecutionLogType... enabledLogTypes) {
        this.logs = synchronizedList(new ArrayList<>());
        this.enabledLogTypes = unmodifiableSet(new HashSet<>(asList(enabledLogTypes)));
    }

    @Override
    public String serviceName() {
        return serviceName;
    }

    @Override
    public String operationName() {
        return operationName;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Execution log for ").append(serviceName).append(".").append(operationName).append(":\n");
        logs.forEach(e -> result.append(e).append("\n"));
        result.setLength(result.length() - 1);
        return result.toString();
    }

    @Override
    public void serviceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public void operationName(String operationName) {
        this.operationName = operationName;
    }

    @Override
    public void add(ExecutionLogType logType, Supplier<String> msg) {
        add(logType, msg, null);
    }

    @Override
    public void add(ExecutionLogType logType, Supplier<String> msg, Throwable throwable) {
        if (enabledLogTypes.contains(logType)) {
            logs.add(ExecutionLogEntry.builder()
                                      .type(logType)
                                      .time(Instant.now())
                                      .message(msg.get())
                                      .exception(throwable)
                                      .build());
        }
    }
}
