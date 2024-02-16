package io.quarkiverse.clowder.deployment;

import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

@ConfigGroup
public interface ClowderResourcesFeatureConfig extends ClowderFeatureConfig {

    /**
     * The input file in which to place the user-defined ClowdApp template which will be used as inputs to populate the
     * generated clowdapp.yaml. The file must contain a resource of type Template.
     */
    @WithDefault("src/main/resources/clowdapp.yaml")
    String inputFile();

    /**
     * Allow to overwrite the kafka port of the clowder config.
     */
    @WithDefault("deploy/clowdapp.yaml")
    String outputFile();

    /**
     * The name of the ClowdEnvironment providing the services.
     */
    Optional<String> envName();

    /**
     * Labels to be included in the generated ClowdApp resource.
     */
    Map<String, String> labels();

    /**
     * The name of the deployment that will use the ClowdApp resource.
     */
    @WithDefault("service")
    String deploymentName();

    /**
     * The number of replicas that the ClowdApp resource will use.
     */
    @WithDefault("1")
    int replicas();

    /**
     * Image of the ClowdApp resource. If not provided, it will use the generated image from the Quarkus Container Image
     * extensions.
     * If there is no a Quarkus Container Image configured, it will fail.
     */
    Optional<String> image();
}
