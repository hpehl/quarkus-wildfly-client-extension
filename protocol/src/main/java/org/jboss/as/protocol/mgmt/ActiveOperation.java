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

import org.jboss.threads.AsyncFuture;
import org.xnio.Cancellable;

/**
 * Encapsulates information about a currently active operation, which can require multiple messages exchanged between the client
 * and the server.
 * <p>
 * An attachment is optional, but can be used to maintain a shared state between multiple requests. It can be accessed using the
 * {@link org.jboss.as.protocol.mgmt.ManagementRequestContext#getAttachment()} from the request handler.
 * <p>
 * An operation is seen as active until one of the methods on the {@link ActiveOperation.ResultHandler} are called.
 *
 * @param <T> the result type
 * @param <A> the attachment type
 * @author Emanuel Muckenhuber
 */
public interface ActiveOperation<T, A> {

    /**
     * Get the batch id.
     *
     * @return the batch id
     */
    Integer getOperationId();

    /**
     * Get the result handler for this request.
     *
     * @return the result handler
     */
    ResultHandler<T> getResultHandler();

    /**
     * Get the attachment.
     *
     * @return the attachment
     */
    A getAttachment();

    /**
     * Get the result.
     *
     * @return the future result
     */
    AsyncFuture<T> getResult();

    /**
     * Add a cancellation handler.
     *
     * @param cancellable the cancel handler
     */
    void addCancellable(final Cancellable cancellable);

    /**
     * Handler for the operation result or to mark the operation as cancelled or failed.
     *
     * @param <T> the result type
     */
    interface ResultHandler<T> {

        /**
         * Set the result.
         *
         * @param result the result
         * @return {@code true} if the result was successfully set, or {@code false} if a result was already set
         */
        boolean done(T result);

        /**
         * Mark the operation as failed.
         *
         * @param t the exception
         * @return {@code true} if the result was successfully set, or {@code false} if a result was already set
         */
        boolean failed(Throwable t);

        /**
         * Cancel the operation.
         */
        void cancel();
    }

    /**
     * A callback indicating when an operation is complete. This is not part of the ActiveOperation API itself, but rather is
     * often provided by callers that trigger instantiation of an ActiveOperation in a process.
     *
     * @param <T> the result type
     */
    interface CompletedCallback<T> {

        /**
         * The operation completed successfully.
         *
         * @param result the result
         */
        void completed(T result);

        /**
         * The operation failed.
         *
         * @param e the failure
         */
        void failed(Exception e);

        /**
         * The operation was cancelled before being able to complete.
         */
        void cancelled();
    }

}
