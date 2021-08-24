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

import java.time.Instant;
import java.util.Optional;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;
import software.amazon.awssdk.utils.internal.executionlog.DefaultExecutionLogEntry;

public interface ExecutionLogEntry extends ToCopyableBuilder<ExecutionLogEntry.Builder, ExecutionLogEntry> {
    static Builder builder() {
        return new DefaultExecutionLogEntry.Builder();
    }

    ExecutionLogType type();

    Instant time();

    String message();

    Optional<Throwable> exception();

    interface Builder extends CopyableBuilder<Builder, ExecutionLogEntry> {
        Builder type(ExecutionLogType logType);

        Builder time(Instant time);

        Builder message(String entry);

        Builder exception(Throwable exception);

        ExecutionLogEntry build();
    }


}
