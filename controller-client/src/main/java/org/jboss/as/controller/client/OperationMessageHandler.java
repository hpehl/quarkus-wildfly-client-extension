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

import static org.jboss.as.controller.client.logging.ControllerClientLogger.ROOT_LOGGER;

/**
 * An operation message handler for handling progress reports.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface OperationMessageHandler {

    /**
     * Handle an operation progress report.
     *
     * @param severity the severity of the message
     * @param message the message
     */
    void handleReport(MessageSeverity severity, String message);

    /**
     * An operation message handler which logs to the current system log.
     */
    OperationMessageHandler logging = new OperationMessageHandler() {

        public void handleReport(final MessageSeverity severity, final String message) {
            switch (severity) {
                case ERROR:
                    ROOT_LOGGER.error(message);
                    break;
                case WARN:
                    ROOT_LOGGER.warn(message);
                    break;
                case INFO:
                default:
                    ROOT_LOGGER.trace(message);
                    break;
            }
        }
    };

    /**
     * A noop operation message handler, which discards all received messages.
     */
    OperationMessageHandler DISCARD = new OperationMessageHandler() {

        @Override
        public void handleReport(MessageSeverity severity, String message) {
            //
        }
    };

}
