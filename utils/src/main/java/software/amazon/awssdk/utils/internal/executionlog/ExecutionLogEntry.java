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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.executionlog.ExecutionLogType;

@SdkInternalApi
public final class ExecutionLogEntry {
    private final ExecutionLogType logType;
    private final Instant time;
    private final String message;
    private final Throwable exception;

    private ExecutionLogEntry(Builder builder) {
        this.logType = Validate.paramNotNull(builder.logType, "logType");
        this.time = Validate.paramNotNull(builder.time, "time");
        this.message = Validate.paramNotNull(builder.message, "message");
        this.exception = builder.exception;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ExecutionLogType type() {
        return logType;
    }

    public Instant time() {
        return time;
    }

    public String message() {
        return message;
    }

    public Optional<Throwable> exception() {
        return Optional.ofNullable(exception);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(time).append(" - [").append(logType).append("] ").append(message);
        if (exception != null) {
            StringWriter writer = new StringWriter();
            exception.printStackTrace(new PrintWriter(writer));
            result.append("\n").append(writer);
        }
        return result.toString();
    }

    public static final class Builder {
        private ExecutionLogType logType;
        private Instant time;
        private String message;
        private Throwable exception;

        public Builder() {
        }

        public Builder type(ExecutionLogType logType) {
            this.logType = logType;
            return this;
        }

        public Builder time(Instant time) {
            this.time = time;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder exception(Throwable exception) {
            this.exception = exception;
            return this;
        }

        public ExecutionLogEntry build() {
            return new ExecutionLogEntry(this);
        }
    }
}
