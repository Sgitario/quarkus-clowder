package io.quarkiverse.clowder.deployment.resources;

import com.redhat.cloud.v1alpha1.ClowdApp;
import com.redhat.cloud.v1alpha1.clowdappspec.Deployments;

import io.fabric8.openshift.api.model.Template;

public class ReplicasDeploymentClowdAppResourceDecorator extends DeploymentClowdAppResourceDecorator {

    @Override
    public void decorate(Deployments deployment, ClowdApp clowdApp, Template template, ResourceDecoratorContext context) {
        deployment.setReplicas(context.config().resources().replicas());
    }
}
