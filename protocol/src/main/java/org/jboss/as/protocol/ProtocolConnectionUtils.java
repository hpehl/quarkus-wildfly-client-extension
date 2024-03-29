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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.jboss.as.protocol.logging.ProtocolLogger;
import org.jboss.remoting3.Connection;
import org.jboss.remoting3.Endpoint;
import org.wildfly.security.auth.client.AuthenticationConfiguration;
import org.wildfly.security.auth.client.AuthenticationContext;
import org.wildfly.security.auth.client.AuthenticationContextConfigurationClient;
import org.wildfly.security.auth.client.CallbackKind;
import org.wildfly.security.auth.client.MatchRule;
import org.wildfly.security.sasl.SaslMechanismSelector;
import org.wildfly.security.sasl.localuser.LocalUserClient;
import org.xnio.IoFuture;
import org.xnio.Option;
import org.xnio.OptionMap;
import org.xnio.Options;

import static java.security.AccessController.doPrivileged;

/**
 * Protocol Connection utils.
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @author Emanuel Muckenhuber
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
public class ProtocolConnectionUtils {

    private static final String JBOSS_LOCAL_USER = "JBOSS-LOCAL-USER";
    private static final Map<String, String> QUIET_LOCAL_AUTH = Collections.singletonMap(LocalUserClient.QUIET_AUTH,
            "true");

    private static final AuthenticationContextConfigurationClient AUTH_CONFIGURATION_CLIENT = doPrivileged(
            AuthenticationContextConfigurationClient.ACTION);

    /**
     * Connect.
     *
     * @param configuration the connection configuration
     * @return the future connection
     * @throws IOException
     */
    public static IoFuture<Connection> connect(final ProtocolConnectionConfiguration configuration) throws IOException {
        return connect(configuration.getCallbackHandler(), configuration);
    }

    public static IoFuture<Connection> connect(final ProtocolConnectionConfiguration configuration,
            CallbackHandler handler) throws IOException {
        final ProtocolConnectionConfiguration config = ProtocolConnectionConfiguration.copy(configuration);
        config.setCallbackHandler(handler);
        return connect(config);
    }

    public static IoFuture<Connection> connect(final ProtocolConnectionConfiguration configuration,
            CallbackHandler handler, Map<String, String> saslOptions, SSLContext sslContext) throws IOException {
        final ProtocolConnectionConfiguration config = ProtocolConnectionConfiguration.copy(configuration);
        config.setCallbackHandler(handler);
        config.setSaslOptions(saslOptions);
        config.setSslContext(sslContext);
        return connect(config);
    }

    /**
     * Connect sync.
     *
     * @param configuration the protocol configuration
     * @return the connection
     * @throws IOException
     */
    public static Connection connectSync(final ProtocolConnectionConfiguration configuration) throws IOException {
        long timeoutMillis = configuration.getConnectionTimeout();
        CallbackHandler handler = configuration.getCallbackHandler();
        final CallbackHandler actualHandler;
        ProtocolTimeoutHandler timeoutHandler = configuration.getTimeoutHandler();
        // Note: If a client supplies a ProtocolTimeoutHandler it is taking on full responsibility for timeout management.
        if (timeoutHandler == null) {
            GeneralTimeoutHandler defaultTimeoutHandler = new GeneralTimeoutHandler();
            // No point wrapping our AnonymousCallbackHandler.
            actualHandler = handler != null ? new WrapperCallbackHandler(defaultTimeoutHandler, handler) : null;
            timeoutHandler = defaultTimeoutHandler;
        } else {
            actualHandler = handler;
        }

        final IoFuture<Connection> future = connect(actualHandler, configuration);

        IoFuture.Status status = timeoutHandler.await(future, timeoutMillis);

        if (status == IoFuture.Status.DONE) {
            return future.get();
        }
        if (status == IoFuture.Status.FAILED) {
            throw ProtocolLogger.ROOT_LOGGER.failedToConnect(configuration.getUri(), future.getException());
        }
        throw ProtocolLogger.ROOT_LOGGER.couldNotConnect(configuration.getUri());
    }

    public static Connection connectSync(final ProtocolConnectionConfiguration configuration, CallbackHandler handler)
            throws IOException {
        final ProtocolConnectionConfiguration config = ProtocolConnectionConfiguration.copy(configuration);
        config.setCallbackHandler(handler);
        return connectSync(config);
    }

    public static Connection connectSync(final ProtocolConnectionConfiguration configuration, CallbackHandler handler,
            Map<String, String> saslOptions, SSLContext sslContext) throws IOException {
        final ProtocolConnectionConfiguration config = ProtocolConnectionConfiguration.copy(configuration);
        config.setCallbackHandler(handler);
        config.setSaslOptions(saslOptions);
        config.setSslContext(sslContext);
        return connectSync(config);
    }

    private static final EnumSet<CallbackKind> DEFAULT_CALLBACK_KINDS = EnumSet.of(
            CallbackKind.PRINCIPAL,
            CallbackKind.CREDENTIAL,
            CallbackKind.REALM);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static IoFuture<Connection> connect(final CallbackHandler handler,
            final ProtocolConnectionConfiguration configuration) throws IOException {
        configuration.validate();
        final Endpoint endpoint = configuration.getEndpoint();
        final URI uri = configuration.getUri();
        String clientBindAddress = configuration.getClientBindAddress();

        AuthenticationContext captured = AuthenticationContext.captureCurrent();
        AuthenticationConfiguration mergedConfiguration = AUTH_CONFIGURATION_CLIENT.getAuthenticationConfiguration(uri,
                captured);
        if (handler != null) {
            mergedConfiguration = mergedConfiguration.useCallbackHandler(handler, DEFAULT_CALLBACK_KINDS);
        }

        Map<String, String> saslOptions = configuration.getSaslOptions();
        mergedConfiguration = configureSaslMechanisms(saslOptions, isLocal(uri), mergedConfiguration);

        // Pass through any other SASL options from the ProtocolConnectionConfiguration
        // When we merge these, any pre-existing options already associated with the
        // AuthenticationConfiguration will take precedence.
        if (saslOptions != null) {
            saslOptions = new HashMap<>(saslOptions);
            // Drop SASL_DISALLOWED_MECHANISMS which we already handled
            saslOptions.remove(Options.SASL_DISALLOWED_MECHANISMS.getName());
            mergedConfiguration = mergedConfiguration.useSaslMechanismProperties(saslOptions);
        }

        SSLContext sslContext = configuration.getSslContext();
        if (sslContext == null) {
            try {
                sslContext = AUTH_CONFIGURATION_CLIENT.getSSLContext(uri, captured);
            } catch (GeneralSecurityException e) {
                throw ProtocolLogger.ROOT_LOGGER.failedToConnect(uri, e);
            }
        }

        // WFCORE-2342 check for default SSL / TLS options
        final OptionMap.Builder builder = OptionMap.builder();
        OptionMap optionMap = configuration.getOptionMap();
        for (Option option : optionMap) {
            builder.set(option, optionMap.get(option));
        }
        if (optionMap.get(Options.SSL_ENABLED) == null) {
            builder.set(Options.SSL_ENABLED, configuration.isSslEnabled());
        }
        if (optionMap.get(Options.SSL_STARTTLS) == null) {
            builder.set(Options.SSL_STARTTLS, configuration.isUseStartTLS());
        }

        AuthenticationContext authenticationContext = AuthenticationContext.empty();
        authenticationContext = authenticationContext.with(MatchRule.ALL, mergedConfiguration);
        final SSLContext finalSslContext = sslContext;
        authenticationContext = authenticationContext.withSsl(MatchRule.ALL, () -> finalSslContext);

        if (clientBindAddress == null) {
            return endpoint.connect(uri, builder.getMap(), authenticationContext);
        } else {
            InetSocketAddress bindAddr = new InetSocketAddress(clientBindAddress, 0);
            return endpoint.connect(uri, bindAddr, builder.getMap(), authenticationContext);
        }
    }

    private static AuthenticationConfiguration configureSaslMechanisms(Map<String, String> saslOptions, boolean isLocal,
            AuthenticationConfiguration authenticationConfiguration) {
        String[] mechanisms = null;
        String listed;
        if (saslOptions != null && (listed = saslOptions.get(Options.SASL_DISALLOWED_MECHANISMS.getName())) != null) {
            // Disallowed mechanisms were passed via the saslOptions map; need to convert to an XNIO option
            String[] split = listed.split(" ");
            if (isLocal) {
                mechanisms = new String[split.length + 1];
                mechanisms[0] = JBOSS_LOCAL_USER;
                System.arraycopy(split, 0, mechanisms, 1, split.length);
            } else {
                mechanisms = split;
            }
        } else if (!isLocal) {
            mechanisms = new String[] { JBOSS_LOCAL_USER };
        }

        return (mechanisms != null && mechanisms.length > 0) ? authenticationConfiguration.setSaslMechanismSelector(
                SaslMechanismSelector.DEFAULT.forbidMechanisms(mechanisms)) : authenticationConfiguration;
    }

    private static boolean isLocal(final URI uri) {
        try {
            final String hostName = uri.getHost();
            final InetAddress address = InetAddress.getByName(hostName);
            NetworkInterface nic;
            if (address.isLinkLocalAddress()) {
                /*
                 * AS7-6382 On Windows the getByInetAddress was not identifying a NIC where the address did not have the zone
                 * ID, this manual iteration does allow for the address to be matched.
                 */
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                nic = null;
                while (interfaces.hasMoreElements() && nic == null) {
                    NetworkInterface current = interfaces.nextElement();
                    Enumeration<InetAddress> addresses = current.getInetAddresses();
                    while (addresses.hasMoreElements() && nic == null) {
                        InetAddress currentAddress = addresses.nextElement();
                        if (address.equals(currentAddress)) {
                            nic = current;
                        }
                    }
                }
            } else {
                nic = NetworkInterface.getByInetAddress(address);
            }
            return address.isLoopbackAddress() || nic != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static final class WrapperCallbackHandler implements CallbackHandler {

        private final GeneralTimeoutHandler timeoutHandler;
        private final CallbackHandler wrapped;

        WrapperCallbackHandler(final GeneralTimeoutHandler timeoutHandler, final CallbackHandler toWrap) {
            this.timeoutHandler = timeoutHandler;
            this.wrapped = toWrap;
        }

        public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {

            try {
                timeoutHandler.suspendAndExecute(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            wrapped.handle(callbacks);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (UnsupportedCallbackException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } catch (RuntimeException e) {
                if (e.getCause() instanceof IOException) {
                    throw (IOException) e.getCause();
                } else if (e.getCause() instanceof UnsupportedCallbackException) {
                    throw (UnsupportedCallbackException) e.getCause();
                }
                throw e;
            }
        }

    }

}
