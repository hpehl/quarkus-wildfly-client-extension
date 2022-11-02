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

import static org.jboss.as.protocol.mgmt.ProtocolUtils.expectHeader;

/**
 * {@link ManagementRequest} that sends a {@link ManagementProtocol#TYPE_PING} header. Note that this is distinct from the
 * top-level sending of {@link ManagementPingHeader} used by legacy (community 7.0.x) clients.
 *
 * @author Brian Stansberry (c) 2012 Red Hat Inc.
 */
public class ManagementPingRequest extends AbstractManagementRequest<Long, Void> {

    public static final ManagementPingRequest INSTANCE = new ManagementPingRequest();

    @Override
    public byte getOperationType() {
        return ManagementProtocol.TYPE_PING;
    }

    @Override
    protected void sendRequest(ActiveOperation.ResultHandler<Long> resultHandler,
            ManagementRequestContext<Void> context, FlushableDataOutput output) throws IOException {
        // nothing besides the header
    }

    @Override
    public void handleRequest(DataInput input, ActiveOperation.ResultHandler<Long> resultHandler,
            ManagementRequestContext<Void> managementRequestContext) throws IOException {
        expectHeader(input, ManagementProtocol.TYPE_PONG);
        long instanceID = input.readLong();
        resultHandler.done(instanceID);
        expectHeader(input, ManagementProtocol.RESPONSE_END);
    }
}
