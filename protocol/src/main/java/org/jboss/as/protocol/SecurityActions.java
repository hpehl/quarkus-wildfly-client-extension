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

import java.security.PrivilegedAction;

import static java.lang.System.getProperty;
import static java.lang.System.getSecurityManager;
import static java.security.AccessController.doPrivileged;

/**
 * Security actions to access system environment information. No methods in this class are to be made public under any
 * circumstances!
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
final class SecurityActions {

    private SecurityActions() {
    }

    static String getSystemProperty(final String key) {
        return getSecurityManager() == null ? getProperty(key) : doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return getProperty(key);
            }
        });
    }
}
