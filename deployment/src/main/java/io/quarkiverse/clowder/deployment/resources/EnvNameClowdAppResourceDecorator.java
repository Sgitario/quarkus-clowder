package io.quarkiverse.clowder.deployment.resources;

import com.redhat.cloud.v1alpha1.ClowdApp;

import io.fabric8.openshift.api.model.Template;

public class EnvNameClowdAppResourceDecorator extends ClowdAppResourceDecorator {

    private static final String PARAMETER_NAME = "ENV_NAME";

    @Override
    public void decorate(ClowdApp clowdApp, Template template, ResourceDecoratorContext context) {
        context.config().resources().envName().ifPresent(envNameValue -> {
            setParameter(template, PARAMETER_NAME, envNameValue);
            clowdApp.getSpec().setEnvName("${" + PARAMETER_NAME + "}");
        });
    }
}
