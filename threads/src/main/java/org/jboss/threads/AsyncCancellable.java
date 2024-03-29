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
package org.jboss.threads;

/**
 * An interface which supports asynchronous cancellation.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface AsyncCancellable {

    /**
     * Handle an asynchronous cancellation. Does not block, and may be called more than once; in particular, this method may be
     * re-invoked to set the {@code interruptionDesired} flag even if it has already been called without that flag set before.
     * Otherwise, this method is idempotent, and thus may be called more than once without additional effect.
     *
     * @param interruptionDesired {@code true} if interruption of threads is desired
     */
    void asyncCancel(boolean interruptionDesired);
}
