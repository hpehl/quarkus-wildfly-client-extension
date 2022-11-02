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

import java.util.concurrent.ExecutorService;

import javax.security.auth.callback.CallbackHandler;

import org.jboss.as.controller.client.ModelControllerClientConfiguration;

/**
 * @author Emanuel Muckenhuber
 */
public class ClientConfigurationImpl implements ModelControllerClientConfiguration {

    private static final int DEFAULT_CONNECTION_TIMEOUT = 5000;
    private final String address;
    private final String clientBindAddress;
    private final int port;
    private final CallbackHandler handler;
    private final ExecutorService executorService;
    private final String protocol;
    private final boolean shutdownExecutor;
    private final int connectionTimeout;

    public ClientConfigurationImpl(String address, int port, CallbackHandler handler, ExecutorService executorService,
            boolean shutdownExecutor, final int connectionTimeout, final String protocol, String clientBindAddress) {
        this.address = address;
        this.port = port;
        this.handler = handler;
        this.executorService = executorService;
        this.shutdownExecutor = shutdownExecutor;
        this.protocol = protocol;
        this.clientBindAddress = clientBindAddress;
        this.connectionTimeout = connectionTimeout > 0 ? connectionTimeout : DEFAULT_CONNECTION_TIMEOUT;
    }

    @Override
    public String getHost() {
        return address;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public CallbackHandler getCallbackHandler() {
        return handler;
    }

    @Override
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    @Override
    public ExecutorService getExecutor() {
        return executorService;
    }

    @Override
    public void close() {
        if (shutdownExecutor && executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public String getClientBindAddress() {
        return clientBindAddress;
    }
}
