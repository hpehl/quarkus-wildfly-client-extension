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

/**
 * @author John Bailey
 */
public interface ManagementProtocol {

    // Headers
    byte[] SIGNATURE = { Byte.MAX_VALUE, Byte.MIN_VALUE, Byte.MAX_VALUE, Byte.MIN_VALUE };
    int VERSION_FIELD = 0x00; // The version field header
    int VERSION = 2; // The current protocol version

    byte TYPE = 0x1;
    byte TYPE_REQUEST = 0x2;
    byte TYPE_RESPONSE = 0x3;
    byte TYPE_BYE_BYE = 0x4;
    byte TYPE_PING = 0x5;
    byte TYPE_PONG = 0x6;

    byte REQUEST_ID = 0x10;
    byte BATCH_ID = 0x11;
    byte OPERATION_ID = 0x12;
    byte ONE_WAY = 0x13;
    byte REQUEST_BODY = 0x14;
    byte REQUEST_END = 0x15;

    byte RESPONSE_ID = 0x20;
    byte RESPONSE_TYPE = 0x21;
    byte RESPONSE_BODY = 0x22;
    byte RESPONSE_ERROR = 0x23;
    byte RESPONSE_END = 0x24;
}
