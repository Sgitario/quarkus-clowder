package io.quarkiverse.clowder.deployment.resources;

import java.util.ArrayList;

import com.redhat.cloud.v1alpha1.ClowdApp;
import com.redhat.cloud.v1alpha1.clowdappspec.Deployments;
import com.redhat.cloud.v1alpha1.clowdappspec.deployments.WebServices;
import com.redhat.cloud.v1alpha1.clowdappspec.deployments.webservices.Public;

import io.fabric8.openshift.api.model.Template;

public abstract class DeploymentClowdAppResourceDecorator extends ClowdAppResourceDecorator {

    public abstract void decorate(Deployments deployment, ClowdApp clowdApp, Template template,
            ResourceDecoratorContext context);

    @Override
    public void decorate(ClowdApp clowdApp, Template template, ResourceDecoratorContext context) {
        String deploymentName = context.config().resources().deploymentName();
        if (clowdApp.getSpec().getDeployments() == null) {
            clowdApp.getSpec().setDeployments(new ArrayList<>());
        }

        Deployments deployment = clowdApp.getSpec().getDeployments()
                .stream()
                .filter(d -> deploymentName.equals(d.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Deployments d = new Deployments();
                    d.setName(deploymentName);
                    d.setWebServices(new WebServices());
                    d.getWebServices().set_public(new Public());
                    d.getWebServices().get_public().setEnabled(true);
                    clowdApp.getSpec().getDeployments().add(d);
                    return d;
                });

        decorate(deployment, clowdApp, template, context);
    }
}
