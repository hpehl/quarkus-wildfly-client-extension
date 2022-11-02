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

/**
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public interface ModelControllerProtocol {

    byte CLOSE_INPUTSTREAM_REQUEST = 0x44;
    byte EXECUTE_ASYNC_CLIENT_REQUEST = 0x45;
    byte EXECUTE_CLIENT_REQUEST = 0x46;
    byte EXECUTE_TX_REQUEST = 0x47;
    byte HANDLE_REPORT_REQUEST = 0x48;
    byte GET_INPUTSTREAM_REQUEST = 0x4C;
    byte CANCEL_ASYNC_REQUEST = 0x4D;
    byte COMPLETE_TX_REQUEST = 0x4E;
    // This was never used in a .Final release, so byte can be re-used
    // byte GET_SUBJECT_REQUEST = 0x4F;
    byte GET_CHUNKED_INPUTSTREAM_REQUEST = 0x4F;

    // This was never used in a .Final release before WF 9, so it's repurposed now
    byte PARAM_END = 0x60;
    byte PARAM_OPERATION = 0x61;
    byte PARAM_MESSAGE_SEVERITY = 0x62;
    byte PARAM_MESSAGE = 0x63;
    byte PARAM_RESPONSE = 0x64;
    byte PARAM_INPUTSTREAMS_LENGTH = 0x65;
    byte PARAM_INPUTSTREAM_INDEX = 0x66;
    byte PARAM_INPUTSTREAM_LENGTH = 0x67;
    byte PARAM_INPUTSTREAM_CONTENTS = 0x68;
    // This was never used in a .Final release, so byte can be re-used
    // byte PARAM_PREPARED = 0x69;
    byte PARAM_COMMIT = 0x70;
    byte PARAM_ROLLBACK = 0x71;
    // The tx response params
    byte PARAM_OPERATION_FAILED = 0x49;
    byte PARAM_OPERATION_COMPLETED = 0x4A;
    byte PARAM_OPERATION_PREPARED = 0x4B;
    // The propagated identity params
    byte PARAM_IDENTITY_LENGTH = 0x50;
    byte PARAM_IN_VM_CALL = 0x51;

}
