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

import org.jboss.remoting3.Attachments;
import org.jboss.remoting3.Channel;
import org.jboss.threads.AsyncFuture;

/**
 * Associates a remoting {@code Channel} to a management client.
 *
 * @author Emanuel Muckenhuber
 */
public interface ManagementChannelAssociation {

    /**
     * Execute a management request.
     *
     * @param request the request
     * @param attachment the attachment
     * @param <T> the result type
     * @param <A> the attachment type
     * @return the created active operation
     * @throws IOException
     */
    <T, A> ActiveOperation<T, A> executeRequest(final ManagementRequest<T, A> request, A attachment) throws IOException;

    /**
     * Execute a management request.
     *
     * @param request the request
     * @param attachment the attachment
     * @param callback the completion listener
     * @param <T> the result type
     * @param <A> the attachment type
     * @return the created active operation
     * @throws IOException
     */
    <T, A> ActiveOperation<T, A> executeRequest(final ManagementRequest<T, A> request, A attachment,
            ActiveOperation.CompletedCallback<T> callback) throws IOException;

    /**
     * Execute a request based on an existing active operation.
     *
     * @param operationId the operation-id of the existing active operation
     * @param request the request
     * @param <T> the request type
     * @param <A> the attachment type
     * @return the future result
     * @throws IOException
     */
    <T, A> AsyncFuture<T> executeRequest(final Integer operationId, final ManagementRequest<T, A> request)
            throws IOException;

    /**
     * Execute a request based on an existing active operation.
     *
     * @param operation the active operation
     * @param request the request
     * @param <T> the result type
     * @param <A> the attachment type
     * @return the future result
     * @throws IOException
     */
    <T, A> AsyncFuture<T> executeRequest(final ActiveOperation<T, A> operation, final ManagementRequest<T, A> request)
            throws IOException;

    /**
     * Initialize a new {@link ActiveOperation}, for subsequent use with
     * {@link #executeRequest(ActiveOperation, ManagementRequest)}.
     *
     * @param attachment the attachment
     * @param callback the completion listener
     * @param <T> the result type
     * @param <A> the attachment type
     * @return the created active operation
     * @throws IOException
     */
    <T, A> ActiveOperation<T, A> initializeOperation(A attachment, ActiveOperation.CompletedCallback<T> callback)
            throws IOException;

    /**
     * Get the underlying remoting channel associated with this context.
     *
     * @return the channel
     * @throws IOException
     */
    Channel getChannel() throws IOException;

    /**
     * Get the attachments object that can be used to share state between users of this object.
     *
     * @return the attachments
     */
    Attachments getAttachments();

}
