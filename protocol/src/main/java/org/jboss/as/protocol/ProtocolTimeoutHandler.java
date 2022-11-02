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

import org.xnio.IoFuture;

/**
 * An implementation of this interface can be provided by calling clients where they wish to supply their own implementation to
 * handle timeouts whilst establishing a connection.
 * <p>
 * The general purpose of this is for clients that wish to take into account additional factors during connection such as user
 * think time / value entry.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
public interface ProtocolTimeoutHandler {

    /**
     * Wait for the specified time on the supplied {@link IoFuture}, taking into account that some of this time could actually
     * not be related to the establishment of the connection but instead some local task such as user think time.
     *
     * @param future - The {@link IoFuture} to wait on.
     * @param timeoutMillis - The configures timeout in milliseconds.
     * @return The {@link IoFuture.Status} when available or at the time the timeout is reached - whichever is soonest.
     */
    IoFuture.Status await(IoFuture<?> future, long timeoutMillis);

}
