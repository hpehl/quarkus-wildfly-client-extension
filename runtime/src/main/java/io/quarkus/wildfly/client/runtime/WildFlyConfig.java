package io.quarkus.wildfly.client.runtime;

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
    @ConfigItem(defaultValue = "127.0.0.1")
    public Optional<String> host;

    /**
     * Configures the port.
     */
    @ConfigItem(defaultValue = "9990")
    public OptionalInt port;

    /**
     * Configures the username.
     */
    @ConfigItem
    public Optional<String> username;

    /**
     * Configures the password.
     */
    @ConfigItem
    public Optional<String> password;
}
