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

/**
 * A management request.
 *
 * @author John Bailey
 * @author Kabir Khan
 * @author Emanuel Muckenhuber
 */
public interface ManagementRequest<T, A> extends ManagementResponseHandler<T, A> {

    /**
     * The operation type.
     *
     * @return the operation type
     */
    byte getOperationType();

    /**
     * Send the request.
     *
     * @param resultHandler the result handler
     * @param context the request context
     * @throws IOException for any error
     */
    void sendRequest(ActiveOperation.ResultHandler<T> resultHandler, ManagementRequestContext<A> context)
            throws IOException;

}
