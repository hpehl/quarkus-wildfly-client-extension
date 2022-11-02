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
package org.jboss.as.controller.client;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.jboss.dmr.ModelNode;

/**
 * Encapsulates a detyped operation request passed in to the model controller, along with any attachments associated with the
 * request.
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @author Brian Stansberry (c) 2011 Red Hat Inc.
 */
public interface Operation extends OperationAttachments {

    /**
     * The detyped operation to execute
     *
     * @return the operation
     */
    ModelNode getOperation();

    /**
     * Clones this operation.
     */
    @Deprecated
    Operation clone();

    /**
     * Clones this operation, but overrides the raw operation node
     */
    @Deprecated
    Operation clone(ModelNode operation);

    /** Factory methods for creating {@code Operation}s */
    class Factory {

        /**
         * Create a simple operation with no stream attachments.
         *
         * @param operation the DMR operation. Cannot be {@code null}
         * @return the operation. Will not be {@code null}
         */
        public static Operation create(final ModelNode operation) {
            return create(operation, Collections.emptyList());
        }

        /**
         * Create a simple operation with stream attachments. The streams will not be
         * {@link OperationAttachments#isAutoCloseStreams() automatically closed} when operation execution is completed.
         *
         * @param operation the DMR operation. Cannot be {@code null}
         * @param attachments the stream attachments. Cannot be {@code null}
         * @return the operation. Will not be {@code null}
         */
        public static Operation create(final ModelNode operation, final List<InputStream> attachments) {
            return new OperationImpl(operation, attachments);
        }

        /**
         * Create an operation using the given streams and be {@link OperationAttachments#isAutoCloseStreams() auto-close
         * streams} setting.
         *
         * @param operation the DMR operation. Cannot be {@code null}
         * @param attachments the stream attachments. Cannot be {@code null}
         * @param autoCloseStreams {@code true} if the attached streams should be automatically closed when operation execution
         *        completes
         * @return the operation. Will not be {@code null}
         */
        public static Operation create(final ModelNode operation, final List<InputStream> attachments,
                final boolean autoCloseStreams) {
            return new OperationImpl(operation, attachments, autoCloseStreams);
        }
    }
}
