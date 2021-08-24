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

import java.util.List;
import java.util.function.Supplier;
import software.amazon.awssdk.utils.executionlog.ExecutionLog;
import software.amazon.awssdk.utils.executionlog.ExecutionLogEntry;
import software.amazon.awssdk.utils.executionlog.ExecutionLogType;

public class NoOpExecutionLog implements ExecutionLog {
    @Override
    public void add(ExecutionLogType entryLevel, Supplier<String> msg) {
    }

    @Override
    public void add(ExecutionLogType entryLogLevel, Supplier<String> msg, Throwable throwable) {
    }

    @Override
    public List<ExecutionLogEntry> entries() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String entriesLog() {
        throw new UnsupportedOperationException();
    }
}
