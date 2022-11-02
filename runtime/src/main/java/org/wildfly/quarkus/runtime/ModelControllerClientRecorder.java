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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.logging.Logger;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class ModelControllerClientRecorder {

    private static final Logger LOGGER = Logger.getLogger(ModelControllerClientRecorder.class);
    private static volatile ModelControllerClient client;

    public void createClient(BeanContainer container, WildFlyConfig config, ShutdownContext shutdown) {
        try {
            String host = config.host.orElse("127.0.0.1");
            int port = config.port.orElse(9990);
            InetAddress address = InetAddress.getByName(host);
            String username = config.username.orElse("admin");
            String password = config.username.orElse("admin");
            client = ModelControllerClient.Factory.create(address, port, callbacks -> {
                for (Callback current : callbacks) {
                    if (current instanceof NameCallback) {
                        NameCallback ncb = (NameCallback) current;
                        ncb.setName(username);
                    } else if (current instanceof PasswordCallback) {
                        PasswordCallback pcb = (PasswordCallback) current;
                        pcb.setPassword(password.toCharArray());
                    } else if (current instanceof RealmCallback) {
                        RealmCallback rcb = (RealmCallback) current;
                        rcb.setText(rcb.getDefaultText());
                    } else {
                        throw new UnsupportedCallbackException(current);
                    }
                }
            });
            ModelControllerClientProducer producer = container.instance(ModelControllerClientProducer.class);
            producer.initialize(client);
            shutdown.addShutdownTask(this::close);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Unable to connect to WildFly: " + e.getMessage());
        }
    }

    void close() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                LOGGER.errorf("Unable to close WildFly client: %s", e.getMessage());
            }
        }
    }
}
