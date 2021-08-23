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

package software.amazon.awssdk.utils.internal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import software.amazon.awssdk.utils.ExecutionLog;
import software.amazon.awssdk.utils.ExecutionLogEntry;
import software.amazon.awssdk.utils.ExecutionLogType;

public final class DefaultExecutionLog implements ExecutionLog {
    private final List<ExecutionLogEntry> logs = Collections.synchronizedList(new ArrayList<>());
    private final Set<ExecutionLogType> enabledLogTypes;

    public DefaultExecutionLog(ExecutionLogType... enabledLogTypes) {
        this.enabledLogTypes = new HashSet<>(Arrays.asList(enabledLogTypes));
    }

    @Override
    public void add(ExecutionLogType logType, Supplier<String> msg) {
        add(logType, msg, null);
    }

    @Override
    public void add(ExecutionLogType logType, Supplier<String> msg, Throwable throwable) {
        if (enabledLogTypes.contains(logType)) {
            logs.add(ExecutionLogEntry.builder()
                                      .logType(logType)
                                      .time(Instant.now())
                                      .entry(msg.get())
                                      .exception(throwable)
                                      .build());
        }
    }

    @Override
    public List<ExecutionLogEntry> entries() {
        return Collections.unmodifiableList(logs);
    }

    @Override
    public String entriesLog() {
        StringBuilder result = new StringBuilder();
        entries().forEach(e -> result.append(e).append("\n"));
        result.setLength(result.length() - 1);
        return result.toString();
    }
}
