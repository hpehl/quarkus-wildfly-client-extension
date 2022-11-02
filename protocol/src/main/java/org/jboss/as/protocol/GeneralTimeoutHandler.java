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
package org.jboss.as.protocol;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.xnio.IoFuture;
import org.xnio.IoFuture.Status;

/**
 * A general implementation of {@link ProtocolTimeoutHandler} that takes into account the time taken for {@link Runnable} tasks
 * to be executed.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
public class GeneralTimeoutHandler implements ProtocolTimeoutHandler {

    private volatile boolean thinking = false;
    private final AtomicLong thinkTime = new AtomicLong(0);

    public void suspendAndExecute(final Runnable runnable) {
        thinking = true;
        long startThinking = System.currentTimeMillis();
        try {
            runnable.run();
        } finally {
            thinkTime.addAndGet(System.currentTimeMillis() - startThinking);
            thinking = false;
        }
    }

    @Override
    public Status await(IoFuture<?> future, long timeoutMillis) {
        final long startTime = System.currentTimeMillis();

        IoFuture.Status status = future.await(timeoutMillis, TimeUnit.MILLISECONDS);
        while (status == IoFuture.Status.WAITING) {
            if (thinking) {
                status = future.await(timeoutMillis, TimeUnit.MILLISECONDS);
            } else {
                long timeToWait = (timeoutMillis + thinkTime.get()) - (System.currentTimeMillis() - startTime);
                if (timeToWait > 0) {
                    status = future.await(timeToWait, TimeUnit.MILLISECONDS);
                } else {
                    return status;
                }
            }
        }

        return status;
    }

}