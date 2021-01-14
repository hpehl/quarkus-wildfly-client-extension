package io.quarkus.wildfly.client.runtime;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.logging.Logger;

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
