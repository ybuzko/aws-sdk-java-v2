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
import io.netty.channel.ChannelPromise;
import io.netty.handler.logging.LoggingHandler;
import java.net.SocketAddress;
import java.util.function.Supplier;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.executionlog.ExecutionLog;
import software.amazon.awssdk.utils.executionlog.ExecutionLogType;

public class SdkLoggingHandler extends LoggingHandler {
    private static final Logger log = Logger.loggerFor(SdkLoggingHandler.class);

    private final ExecutionLog executionLog;

    public SdkLoggingHandler(ExecutionLog executionLog) {
        this.executionLog = executionLog;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log(() -> format(ctx, "REGISTERED"));
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log(() -> format(ctx, "UNREGISTERED"));
        ctx.fireChannelUnregistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log(() -> format(ctx, "ACTIVE"));
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log(() -> format(ctx, "INACTIVE"));
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log(() -> format(ctx, "EXCEPTION", cause), cause);
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log(() -> format(ctx, "USER_EVENT", evt));
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        log(() -> format(ctx, "BIND", localAddress));
        ctx.bind(localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
                        SocketAddress localAddress, ChannelPromise promise) throws Exception {
        log(() -> format(ctx, "CONNECT", remoteAddress, localAddress));
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        log(() -> format(ctx, "DISCONNECT"));
        ctx.disconnect(promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        log(() -> format(ctx, "CLOSE"));
        ctx.close(promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        log(() -> format(ctx, "DEREGISTER"));
        ctx.deregister(promise);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log(() -> format(ctx, "READ COMPLETE"));
        ctx.fireChannelReadComplete();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log(() -> format(ctx, "READ", msg));
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log(() -> format(ctx, "WRITE", msg));
        ctx.write(msg, promise);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        log(() -> format(ctx, "WRITEABILITY CHANGED"));
        ctx.fireChannelWritabilityChanged();
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        log(() -> format(ctx, "FLUSH"));
        ctx.flush();
    }

    private void log(Supplier<String> message) {
        if (log.isLoggingLevelEnabled("debug")) {
            String resolvedMessage = message.get();
            log.debug(() -> resolvedMessage);
            executionLog.add(ExecutionLogType.WIRE, () -> resolvedMessage);
        } else {
            executionLog.add(ExecutionLogType.WIRE, message);
        }
    }

    private void log(Supplier<String> message, Throwable t) {
        if (log.isLoggingLevelEnabled("debug")) {
            String resolvedMessage = message.get();
            log.debug(() -> resolvedMessage, t);
            executionLog.add(ExecutionLogType.WIRE, () -> resolvedMessage, t);
        } else {
            executionLog.add(ExecutionLogType.WIRE, message, t);
        }
    }
}
