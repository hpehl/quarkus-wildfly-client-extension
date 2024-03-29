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
import java.util.concurrent.Executor;

import org.jboss.remoting3.Channel;

/**
 * The context for handling a management request. Besides providing basic state associated with the request, one of the primary
 * purposes of the {@code ManagementRequestContext} is to support asynchronous task execution by request handlers. Handlers
 * should avoid doing blocking tasks in the remoting threads that invoke them, so the asynchronous task execution facilities of
 * this class allow that work to be offloaded to other threads. Handlers also should be careful about performing IO writes using
 * threads that have invoked management operations, as those threads may be interrupted if the caller cancels the operation, and
 * if that interruption happens during IO the remote channel will be incorrectly closed. The asynchronous task execution
 * facilities of this class allow the IO writes to be done in a separate thread.
 *
 * @param <A> the type of the attachment that can be retrieved from the context
 * @author Emanuel Muckenhuber
 */
public interface ManagementRequestContext<A> {

    /**
     * Get the current batch id for the overall operation.
     *
     * @return the batch id
     */
    Integer getOperationId();

    /**
     * Get any attachment used to maintain shared state across possibly multiple requests associated with a larger overall
     * operation. {@see org.jboss.as.protocol.mgmt.ActiveOperation#getAttachment()}
     *
     * @return the attachment, can be {@code null}
     */
    A getAttachment();

    /**
     * Get the underlying channel.
     *
     * @return the channel
     */
    Channel getChannel();

    /**
     * Get the protocol header.
     *
     * @return the protocol header
     */
    ManagementProtocolHeader getRequestHeader();

    /**
     * Execute a cancellable task at some point in the future, using this object's internal {@link Executor}. Equivalent to
     * {@code executeAsync(task, true)}.
     * <p>
     * If the executor {@link java.util.concurrent.RejectedExecutionException rejects} the task, or if the task itself throws an
     * exception during execution, the {@link ActiveOperation.ResultHandler#failed(Throwable) failed method} of the
     * {@code ResultHander} associated with the request will be invoked, and if it returns {@code true} a failure message will
     * be sent to the remote client.
     * </p>
     *
     * @param task the task
     * @return {@code true} if the task was accepted for execution; {@code false} if the executor rejected it
     */
    boolean executeAsync(final AsyncTask<A> task);

    /**
     * Execute a possibly cancellable task at some point in the future, using this object's internal {@link Executor}.
     * <p>
     * If the executor {@link java.util.concurrent.RejectedExecutionException rejects} the task, or if the task itself throws an
     * exception during execution, the {@link ActiveOperation.ResultHandler#failed(Throwable) failed method} of the
     * {@code ResultHander} associated with the request will be invoked, and if it returns {@code true} a failure message will
     * be sent to the remote client.
     * </p>
     *
     * @param task the task
     * @param cancellable {@code true} if the task can be cancelled as part of overall request cancellation
     * @return {@code true} if the task was accepted for execution; {@code false} if the executor rejected it
     */
    boolean executeAsync(final AsyncTask<A> task, boolean cancellable);

    /**
     * Execute a cancellable task at some point in the future, using the given {@link Executor}. Equivalent to {@code
     * executeAsync(task, true, executor)}.
     * <p>
     * If the executor {@link java.util.concurrent.RejectedExecutionException rejects} the task, or if the task itself throws an
     * exception during execution, the {@link ActiveOperation.ResultHandler#failed(Throwable) failed method} of the
     * {@code ResultHander} associated with the request will be invoked, and if it returns {@code true} a failure message will
     * be sent to the remote client.
     * </p>
     *
     * @param task the task
     * @param executor the executor
     * @return {@code true} if the task was accepted for execution; {@code false} if the executor rejected it
     */
    boolean executeAsync(final AsyncTask<A> task, Executor executor);

    /**
     * Execute a possibly cancellable task at some point in the future, using the given {@link Executor}. Equivalent to
     * {@code executeAsync(task, true, executor)}.
     * <p>
     * If the executor {@link java.util.concurrent.RejectedExecutionException rejects} the task, or if the task itself throws an
     * exception during execution, the {@link ActiveOperation.ResultHandler#failed(Throwable) failed method} of the
     * {@code ResultHander} associated with the request will be invoked, and if it returns {@code true} a failure message will
     * be sent to the remote client.
     * </p>
     *
     * @param task the task
     * @param cancellable {@code true} if the task can be cancelled as part of overall request cancellation
     * @param executor the executor
     * @return {@code true} if the task was accepted for execution; {@code false} if the executor rejected it
     */
    boolean executeAsync(final AsyncTask<A> task, boolean cancellable, Executor executor);

    /**
     * Initiates writing a new message to the remote side, using the given header.
     *
     * @param header the protocol header
     * @return the message output stream to use for writing further data associated with the message
     * @throws IOException
     */
    FlushableDataOutput writeMessage(final ManagementProtocolHeader header) throws IOException;

    /** A task that can be executed asynchronously by a {@link ManagementRequestContext} */
    interface AsyncTask<A> {

        /**
         * Execute the task.
         * <p>
         * If the task throws an exception during execution, the {@link ActiveOperation.ResultHandler#failed(Throwable) failed
         * method} of the {@code ResultHander} associated with the request will be invoked, and if it returns {@code true} a
         * failure message will be sent to the remote client.
         * </p>
         *
         * @param context the request context
         * @throws Exception
         */
        void execute(final ManagementRequestContext<A> context) throws Exception;

    }
}
