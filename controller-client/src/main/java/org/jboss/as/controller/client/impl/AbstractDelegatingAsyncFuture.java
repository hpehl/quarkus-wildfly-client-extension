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

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jboss.threads.AsyncFuture;

/**
 * @author Emanuel Muckenhuber
 */
public abstract class AbstractDelegatingAsyncFuture<T> extends BasicDelegatingAsyncFuture<T, T> {

    public AbstractDelegatingAsyncFuture(AsyncFuture<T> delegate) {
        super(delegate);
    }

    @Override
    public T getUninterruptibly() throws CancellationException, ExecutionException {
        return delegate.getUninterruptibly();
    }

    @Override
    public T getUninterruptibly(long timeout, TimeUnit unit)
            throws CancellationException, ExecutionException, TimeoutException {
        return delegate.getUninterruptibly(timeout, unit);
    }

    public <A> void addListener(Listener<? super T, A> aListener, A attachment) {
        delegate.addListener(aListener, attachment);
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }
}
