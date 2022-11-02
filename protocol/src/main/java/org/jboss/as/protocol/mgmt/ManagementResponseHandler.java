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

/**
 * A local handler for responses to a management request.
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @author Emanuel Muckenhuber
 */
public interface ManagementResponseHandler<T, A> extends ManagementRequestHandler<T, A> {

    /**
     * Handle a failed response.
     *
     * @param header the header
     * @param resultHandler the result handler
     */
    void handleFailed(ManagementResponseHeader header, ActiveOperation.ResultHandler<T> resultHandler);

}
