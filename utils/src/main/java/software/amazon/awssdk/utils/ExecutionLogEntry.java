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

package software.amazon.awssdk.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

public final class ExecutionLogEntry {
    private final ExecutionLogType logType;
    private final Instant time;
    private final String entry;
    private final Throwable exception;

    public ExecutionLogEntry(Builder builder) {
        this.logType = Validate.paramNotNull(builder.logType, "logType");
        this.time = Validate.paramNotNull(builder.time, "time");
        this.entry = Validate.paramNotNull(builder.entry, "entry");
        this.exception = builder.exception;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ExecutionLogType logType() {
        return logType;
    }

    public Instant time() {
        return time;
    }

    public String entry() {
        return entry;
    }

    public Throwable exception() {
        return exception;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(time).append(" - [").append(logType).append("] ").append(entry);
        if (exception != null) {
            StringWriter writer = new StringWriter();
            exception.printStackTrace(new PrintWriter(writer));
            result.append("\n").append(writer);
        }
        return result.toString();
    }

    public static final class Builder {
        private Builder() {
        }

        private ExecutionLogType logType;
        private Instant time;
        private String entry;
        private Throwable exception;

        public Builder logType(ExecutionLogType logType) {
            this.logType = logType;
            return this;
        }

        public Builder time(Instant time) {
            this.time = time;
            return this;
        }

        public Builder entry(String entry) {
            this.entry = entry;
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
