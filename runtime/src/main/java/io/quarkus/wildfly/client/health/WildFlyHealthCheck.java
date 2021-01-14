package io.quarkus.wildfly.client.health;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;

import io.quarkus.arc.Arc;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;

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
