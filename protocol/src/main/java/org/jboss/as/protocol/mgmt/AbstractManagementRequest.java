/*
 *  Copyright 2022 Red Hat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jboss.as.protocol.mgmt;

import java.io.IOException;

import org.jboss.as.protocol.StreamUtils;

/**
 * utility class for creating management requests.
 *
 * @param <T> the response type
 * @param <A> the attachment type
 * @author Emanuel Muckenhuber
 */
public abstract class AbstractManagementRequest<T, A> implements ManagementRequest<T, A> {

    /**
     * Send the request.
     *
     * @param resultHandler the result handler
     * @param context the request context
     * @param output the data output
     * @throws IOException
     */
    protected abstract void sendRequest(ActiveOperation.ResultHandler<T> resultHandler,
            ManagementRequestContext<A> context, FlushableDataOutput output) throws IOException;

    @Override
    public void sendRequest(final ActiveOperation.ResultHandler<T> resultHandler,
            final ManagementRequestContext<A> context) throws IOException {
        final FlushableDataOutput output = context.writeMessage(context.getRequestHeader());
        try {
            sendRequest(resultHandler, context, output);
            output.writeByte(ManagementProtocol.REQUEST_END);
            output.close();
        } finally {
            StreamUtils.safeClose(output);
        }
    }

    @Override
    public void handleFailed(final ManagementResponseHeader header,
            final ActiveOperation.ResultHandler<T> resultHandler) {
        resultHandler.failed(new IOException(header.getError()));
    }

}
