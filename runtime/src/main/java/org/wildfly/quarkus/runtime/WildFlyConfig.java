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
package org.wildfly.quarkus.runtime;

import java.util.Optional;
import java.util.OptionalInt;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "wildfly-client", phase = ConfigPhase.RUN_TIME)
public class WildFlyConfig {

    /**
     * Configures the hostname.
     */
    @ConfigItem(defaultValue = "127.0.0.1") public Optional<String> host;

    /**
     * Configures the port.
     */
    @ConfigItem(defaultValue = "9990") public OptionalInt port;

    /**
     * Configures the username.
     */
    @ConfigItem public Optional<String> username;

    /**
     * Configures the password.
     */
    @ConfigItem public Optional<String> password;
}
