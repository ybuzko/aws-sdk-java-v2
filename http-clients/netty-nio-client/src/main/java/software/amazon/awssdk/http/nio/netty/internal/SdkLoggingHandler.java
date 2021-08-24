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

package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import software.amazon.awssdk.utils.executionlog.ExecutionLog;
import software.amazon.awssdk.utils.executionlog.ExecutionLogType;

public class SdkLoggingHandler extends LoggingHandler {
    private final ExecutionLog executionLog;

    public SdkLoggingHandler(ExecutionLog executionLog) {
        super(LogLevel.DEBUG);
        this.executionLog = executionLog;
    }

    @Override
    protected String format(ChannelHandlerContext ctx, String eventName) {
        String log = super.format(ctx, eventName);
        executionLog.add(ExecutionLogType.WIRE, () -> log);
        return log;
    }

    @Override
    protected String format(ChannelHandlerContext ctx, String eventName, Object arg) {
        String log = super.format(ctx, eventName, arg);
        executionLog.add(ExecutionLogType.WIRE, () -> log);
        return log;
    }

    @Override
    protected String format(ChannelHandlerContext ctx, String eventName, Object firstArg, Object secondArg) {
        String log = super.format(ctx, eventName, firstArg, secondArg);
        executionLog.add(ExecutionLogType.WIRE, () -> log);
        return log;
    }
}
