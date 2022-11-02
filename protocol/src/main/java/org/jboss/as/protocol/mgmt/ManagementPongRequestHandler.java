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

import java.io.DataInput;
import java.io.IOException;

import org.jboss.as.protocol.StreamUtils;

/**
 * {@link ManagementRequestHandlerFactory} for dealing with a {@link ManagementPingRequest}.
 *
 * @author Brian Stansberry (c) 2012 Red Hat Inc.
 */
public class ManagementPongRequestHandler
        implements ManagementRequestHandlerFactory, ManagementRequestHandler<Void, Void> {

    private volatile long connectionId = System.currentTimeMillis();

    @Override
    public ManagementRequestHandler<?, ?> resolveHandler(RequestHandlerChain handlers, ManagementRequestHeader header) {
        final byte operationId = header.getOperationId();
        switch (operationId) {
            case ManagementProtocol.TYPE_PING:
                handlers.registerActiveOperation(header.getBatchId(), null);
                return this;
        }
        return handlers.resolveNext();
    }

    @Override
    public void handleRequest(final DataInput input, final ActiveOperation.ResultHandler<Void> resultHandler,
            final ManagementRequestContext<Void> context) throws IOException {

        final ManagementResponseHeader response = ManagementResponseHeader.create(context.getRequestHeader());

        final FlushableDataOutput output = context.writeMessage(response);
        try {
            output.write(ManagementProtocol.TYPE_PONG);
            output.writeLong(connectionId);
            output.writeByte(ManagementProtocol.RESPONSE_END);
            output.close();
        } finally {
            StreamUtils.safeClose(output);
        }
        resultHandler.done(null);
    }

    /**
     * Update the id we send in pong responses
     */
    public void resetConnectionId() {
        connectionId = System.currentTimeMillis();
    }

    /**
     * Gets the current id we send in pong responses
     *
     * @return
     */
    public long getConnectionId() {
        return connectionId;
    }
}
