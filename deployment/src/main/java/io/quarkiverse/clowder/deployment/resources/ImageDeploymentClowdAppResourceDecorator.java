package io.quarkiverse.clowder.deployment.resources;

import com.redhat.cloud.v1alpha1.ClowdApp;
import com.redhat.cloud.v1alpha1.clowdappspec.Deployments;
import com.redhat.cloud.v1alpha1.clowdappspec.deployments.PodSpec;

import io.fabric8.openshift.api.model.Template;

public class ImageDeploymentClowdAppResourceDecorator extends DeploymentClowdAppResourceDecorator {

    private static final String IMAGE_PARAMETER_NAME = "IMAGE";
    private static final String IMAGE_TAG_PARAMETER_NAME = "IMAGE_TAG";

    @Override
    public void decorate(Deployments deployment, ClowdApp clowdApp, Template template, ResourceDecoratorContext context) {
        if (deployment.getPodSpec() == null) {
            deployment.setPodSpec(new PodSpec());
        }

        int indexOfTag = context.image().lastIndexOf(":");
        if (indexOfTag <= 0) {
            setParameter(template, IMAGE_PARAMETER_NAME, context.image());
            deployment.getPodSpec().setImage("${" + IMAGE_PARAMETER_NAME + "}");
        } else {
            String image = context.image().substring(0, indexOfTag);
            String imageTag = context.image().substring(indexOfTag + 1);
            setParameter(template, IMAGE_PARAMETER_NAME, image);
            setParameter(template, IMAGE_TAG_PARAMETER_NAME, imageTag);
            deployment.getPodSpec().setImage("${" + IMAGE_PARAMETER_NAME + "}:${" + IMAGE_TAG_PARAMETER_NAME + "}");
        }
    }
}
