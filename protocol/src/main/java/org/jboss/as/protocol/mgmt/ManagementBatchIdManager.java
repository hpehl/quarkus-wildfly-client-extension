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

import java.util.HashSet;
import java.util.Set;

/**
 * Responsible for generating new unique batch ids on the server side of a channel. The batch ids are used to group several
 * individual channel requests that make up a larger use case.
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public interface ManagementBatchIdManager {

    /**
     * Block a given batch id, when using shared transports.
     *
     * @param id the id
     * @return true if this did not already contain the id
     */
    boolean lockBatchId(int id);

    /**
     * Creates a batch id. Once the batch has completed {@link ManagementBatchIdManager#freeBatchId} must be called.
     *
     * @return the created batch id
     */
    int createBatchId();

    /**
     * Frees a batch id.
     *
     * @param id the batch id to be freed.
     */
    void freeBatchId(int id);

    class DefaultManagementBatchIdManager implements ManagementBatchIdManager {

        private final Set<Integer> ids = new HashSet<Integer>();

        @Override
        public synchronized boolean lockBatchId(int id) {
            if (ids.contains(id)) {
                return false;
            }
            ids.add(id);
            return true;
        }

        @Override
        public synchronized int createBatchId() {
            int next = (int) (Math.random() * Integer.MAX_VALUE);
            while (ids.contains(next)) {
                next = (int) (Math.random() * Integer.MAX_VALUE);
            }
            ids.add(next);
            return next;
        }

        @Override
        public synchronized void freeBatchId(int id) {
            ids.remove(id);
        }

    }
}
