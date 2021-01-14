package io.quarkus.wildfly.client.deployment;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.smallrye.health.deployment.spi.HealthBuildItem;
import io.quarkus.wildfly.client.runtime.ModelControllerClientProducer;
import io.quarkus.wildfly.client.runtime.ModelControllerClientRecorder;
import io.quarkus.wildfly.client.runtime.WildFlyConfig;

class WildflyClientProcessor {

    private static final String FEATURE = "wildfly";

    @BuildStep
    AdditionalBeanBuildItem registerProducer() {
        return AdditionalBeanBuildItem.unremovableOf(ModelControllerClientProducer.class);
    }

    @BuildStep
    HealthBuildItem addHealthCheck(WildFlyBuildTimeConfig buildTimeConfig) {
        return new HealthBuildItem("io.quarkus.wildfly.client.health.WildFlyHealthCheck",
                buildTimeConfig.healthEnabled, "wildfly-client");
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void build(BuildProducer<FeatureBuildItem> feature,
            ModelControllerClientRecorder recorder,
            BeanContainerBuildItem beanContainer,
            ShutdownContextBuildItem shutdown,
            WildFlyConfig config) {
        feature.produce(new FeatureBuildItem(FEATURE));
        recorder.createClient(beanContainer.getValue(), config, shutdown);
    }
}
