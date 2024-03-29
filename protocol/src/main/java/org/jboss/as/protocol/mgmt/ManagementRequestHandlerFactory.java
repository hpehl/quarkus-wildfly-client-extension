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
 * A {@code ManagementRequestHandler} factory.
 *
 * @author Emanuel Muckenhuber
 */
public interface ManagementRequestHandlerFactory {

    /**
     * Try to resolve the request handler for the given header. If an implementation is unable to resolve a handler itself, it
     * must complete this method by returning the result of {@code handlers.resolveNext()}.
     *
     * @param handlers the handlers chain
     * @param header the request header
     * @return the management request handler
     */
    ManagementRequestHandler<?, ?> resolveHandler(RequestHandlerChain handlers, ManagementRequestHeader header);

    /**
     * A chain of multiple request handler factories, also providing a hook for factories to create new active operations or
     * register the local process instance of a remotely initiated active operation.
     */
    interface RequestHandlerChain {

        /**
         * Create a new active operation. This will generate a new operation-id.
         *
         * @param attachment the optional attachment
         * @param <T> the result type
         * @param <A> the attachment type
         * @return the registered active operation
         */
        <T, A> ActiveOperation<T, A> createActiveOperation(A attachment);

        /**
         * Create a new active operation. This will generate a new operation-id.
         *
         * @param attachment the optional attachment
         * @param callback the completed callback
         * @param <T> the result type
         * @param <A> the attachment type
         * @return the registered active operation
         */
        <T, A> ActiveOperation<T, A> createActiveOperation(A attachment, ActiveOperation.CompletedCallback<T> callback);

        /**
         * Create a new active operation, with a given operation-id obtained from an
         * {@link ManagementRequestHeader#getOperationId() incoming request's header}.
         *
         * @param id the operation-id
         * @param attachment the attachment
         * @param <T> the result type
         * @param <A> the attachment type
         * @return the registered active operation
         * @throws java.lang.IllegalStateException if an operation with the same id is already registered
         */
        <T, A> ActiveOperation<T, A> registerActiveOperation(Integer id, A attachment);

        /**
         * Create a new active operation, with a given operation-idobtained from an
         * {@link ManagementRequestHeader#getOperationId() incoming request's header}.
         *
         * @param id the operation-id
         * @param attachment the attachment
         * @param callback the completed callback
         * @param <T> the result type
         * @param <A> the attachment type
         * @return the registered active operation
         * @throws java.lang.IllegalStateException if an operation with the same id is already registered
         */
        <T, A> ActiveOperation<T, A> registerActiveOperation(Integer id, A attachment,
                ActiveOperation.CompletedCallback<T> callback);

        /**
         * Ask the next factory in the chain to resolve the handler.
         *
         * @return the resolved management request handler
         */
        ManagementRequestHandler<?, ?> resolveNext();

    }

}
