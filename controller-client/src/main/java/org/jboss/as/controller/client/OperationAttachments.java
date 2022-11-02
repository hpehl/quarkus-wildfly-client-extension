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

import java.io.Closeable;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * The operation attachments. This interface extends {@code Closeable} which can be used to close all associated input streams
 * with this attachment.
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public interface OperationAttachments extends Closeable {

    /**
     * Flag indicating whether the streams should be automatically closed once the operation completed.
     *
     * @return {@code true} if the streams are going to be closed, false otherwise
     */
    boolean isAutoCloseStreams();

    /**
     * Input streams associated with the operation
     *
     * @return the streams. If there are none an empty list is returned
     */
    List<InputStream> getInputStreams();

    OperationAttachments EMPTY = new OperationAttachments() {
        @Override
        public boolean isAutoCloseStreams() {
            return false;
        }

        @Override
        public List<InputStream> getInputStreams() {
            return Collections.emptyList();
        }

        @Override
        public void close() {
            //
        }
    };

}
