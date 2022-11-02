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
package org.jboss.as.controller.client.impl;

import java.util.concurrent.TimeUnit;

import org.jboss.threads.AsyncFuture;

/**
 * Base class for {@link org.jboss.as.controller.client.impl.AbstractDelegatingAsyncFuture} and
 * {@link org.jboss.as.controller.client.impl.ConvertingDelegatingAsyncFuture} that handles the simple delegation stuff.
 *
 * @author Brian Stansberry (c) 2014 Red Hat Inc.
 */
abstract class BasicDelegatingAsyncFuture<T, D> implements AsyncFuture<T> {

    final AsyncFuture<D> delegate;

    BasicDelegatingAsyncFuture(AsyncFuture<D> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Status await() throws InterruptedException {
        return delegate.await();
    }

    @Override
    public Status await(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.await(timeout, unit);
    }

    @Override
    public Status awaitUninterruptibly() {
        return delegate.awaitUninterruptibly();
    }

    @Override
    public Status awaitUninterruptibly(long timeout, TimeUnit unit) {
        return delegate.awaitUninterruptibly(timeout, unit);
    }

    @Override
    public Status getStatus() {
        return delegate.getStatus();
    }

    @Override
    public boolean cancel(boolean interruptionDesired) {
        // allow custom cancellation policies
        asyncCancel(interruptionDesired);
        return awaitUninterruptibly() == Status.CANCELLED;
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
        return delegate.isDone();
    }
}
