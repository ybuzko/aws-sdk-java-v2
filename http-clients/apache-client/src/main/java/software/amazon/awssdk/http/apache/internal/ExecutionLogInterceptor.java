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

package software.amazon.awssdk.http.apache.internal;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.protocol.HttpContext;
import software.amazon.awssdk.utils.executionlog.ExecutionLog;
import software.amazon.awssdk.utils.executionlog.ExecutionLogType;

public class ExecutionLogInterceptor implements HttpRequestInterceptor, HttpResponseInterceptor {
    public static final String CONTEXT_KEY = "ExecutionLogInterceptor.ExecutionLog";

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        ExecutionLog log = executionLog(context);
        if (log == null) {
            return;
        }

        log.add(ExecutionLogType.WIRE, () -> "WRITE " + request);

        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest requestWithPayload = (HttpEntityEnclosingRequest) request;
            requestWithPayload.setEntity(new ReadLoggingEntity(requestWithPayload.getEntity(), log));
        }
    }

    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        ExecutionLog log = executionLog(context);
        if (log == null) {
            return;
        }


    }

    private ExecutionLog executionLog(HttpContext context) {
        return (ExecutionLog) context.getAttribute(CONTEXT_KEY);
    }

    private class ReadLoggingEntity extends HttpEntityWrapper {
        private final ExecutionLog log;

        public ReadLoggingEntity(HttpEntity entity, ExecutionLog log) {
            super(entity);
            this.log = log;
        }

        @Override
        public InputStream getContent() throws IOException {
            return new ReadLoggingInputStream(super.getContent(), log);
        }
    }

    private class ReadLoggingInputStream extends FilterInputStream {
        private final ExecutionLog log;

        public ReadLoggingInputStream(InputStream content, ExecutionLog log) {
            super(content);
            this.log = log;
        }

        @Override
        public int read() throws IOException {
            return super.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return super.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return super.read(b, off, len);
        }
    }
}
