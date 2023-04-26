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
package org.wildfly.quarkus.health;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;

import io.quarkus.arc.Arc;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.Bean;

@Readiness
@ApplicationScoped
public class WildFlyHealthCheck implements HealthCheck {
    private static final String DEFAULT_CLIENT = "__default__";
    private final Map<String, ModelControllerClient> clients = new HashMap<>();

    @PostConstruct
    protected void init() {
        Set<Bean<?>> beans = Arc.container().beanManager().getBeans(ModelControllerClient.class);
        for (Bean<?> bean : beans) {
            if (bean.getName() == null) {
                // this is the default mongo client: retrieve it by type
                ModelControllerClient defaultClient = Arc.container().instance(ModelControllerClient.class).get();
                clients.put(DEFAULT_CLIENT, defaultClient);
            } else {
                ModelControllerClient client = (ModelControllerClient) Arc.container().instance(bean.getName()).get();
                clients.put(bean.getName(), client);
            }
        }
    }

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.named("WildFly health check").up();
        for (Map.Entry<String, ModelControllerClient> client : clients.entrySet()) {
            boolean isDefault = DEFAULT_CLIENT.equals(client.getKey());
            ModelControllerClient mcc = client.getValue();
            try {
                String wildFlyClientName = isDefault ? "default" : client.getKey();
                ModelNode operation = Operations.createReadResourceOperation(new ModelNode());
                ModelNode payload = mcc.execute(operation);
                if (Operations.isSuccessfulOutcome(payload)) {
                    builder.up().withData(wildFlyClientName, payload.get("result").get("name").asString());
                } else {
                    builder.down().withData("reason", Operations.getFailureDescription(payload).asString("n/a"));
                }
            } catch (Exception e) {
                return builder.down().withData("reason", e.getMessage()).build();
            }
        }
        return builder.build();
    }
}
