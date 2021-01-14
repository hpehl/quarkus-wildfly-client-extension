package io.quarkus.wildfly.client.runtime;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.jboss.as.controller.client.ModelControllerClient;

@ApplicationScoped
public class ModelControllerClientProducer {

    private ModelControllerClient client;

    synchronized void initialize(ModelControllerClient client) {
        this.client = client;
    }

    @Singleton
    @Produces
    public synchronized ModelControllerClient modelControllerClient() {
        return client;
    }
}
