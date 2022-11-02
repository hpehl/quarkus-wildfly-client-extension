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
package org.wildfly.quarkus.deployment;

import org.wildfly.quarkus.runtime.ModelControllerClientProducer;
import org.wildfly.quarkus.runtime.ModelControllerClientRecorder;
import org.wildfly.quarkus.runtime.WildFlyConfig;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.smallrye.health.deployment.spi.HealthBuildItem;

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
