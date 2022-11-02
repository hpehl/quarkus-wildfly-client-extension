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

/**
 * A handler for incoming requests.
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @author Emanuel Muckenhuber
 */
public interface ManagementRequestHandler<T, A> {

    /**
     * Handle a request.
     *
     * @param input the data input
     * @param resultHandler the result handler which may be used to mark the operation as complete
     * @param context the request context
     * @throws IOException
     */
    void handleRequest(final DataInput input, final ActiveOperation.ResultHandler<T> resultHandler,
            final ManagementRequestContext<A> context) throws IOException;

}
