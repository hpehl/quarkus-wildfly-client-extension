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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.as.protocol.StreamUtils;
import org.jboss.dmr.ModelNode;
import org.wildfly.common.Assert;

class OperationImpl implements Operation {

    private final boolean autoCloseStreams;
    private final ModelNode operation;
    private final List<InputStream> inputStreams;

    OperationImpl(final ModelNode operation, final List<InputStream> inputStreams) {
        this(operation, inputStreams, false);
    }

    OperationImpl(final ModelNode operation, final List<InputStream> inputStreams, final boolean autoCloseStreams) {
        this.operation = operation;
        this.inputStreams = inputStreams;
        this.autoCloseStreams = autoCloseStreams;
    }

    @Override
    public boolean isAutoCloseStreams() {
        return autoCloseStreams;
    }

    @Override
    public ModelNode getOperation() {
        return operation;
    }

    @Override
    public List<InputStream> getInputStreams() {
        if (inputStreams == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(inputStreams);
    }

    @Override
    @Deprecated
    public Operation clone() {
        List<InputStream> streamsCopy = inputStreams == null ? null : new ArrayList<InputStream>(inputStreams);
        return new OperationImpl(operation.clone(), streamsCopy);
    }

    @Override
    @Deprecated
    public Operation clone(final ModelNode operation) {
        Assert.checkNotNullParam("operation", operation);
        List<InputStream> streamsCopy = inputStreams == null ? null : new ArrayList<InputStream>(inputStreams);
        return new OperationImpl(operation, streamsCopy);
    }

    @Override
    public void close() throws IOException {
        final List<InputStream> streams = getInputStreams();
        for (final InputStream stream : streams) {
            StreamUtils.safeClose(stream);
        }
    }
}
