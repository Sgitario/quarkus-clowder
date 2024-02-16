package io.quarkiverse.clowder.deployment.resources;

import com.redhat.cloud.v1alpha1.ClowdApp;
import com.redhat.cloud.v1alpha1.ClowdAppSpec;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.openshift.api.model.Template;

public class InitializeClowdAppResourceDecorator extends ClowdAppResourceDecorator {
    @Override
    public void decorate(ClowdApp clowdApp, Template template, ResourceDecoratorContext context) {
        if (clowdApp.getMetadata() == null) {
            clowdApp.setMetadata(new ObjectMeta());
        }

        if (clowdApp.getMetadata().getName() == null) {
            clowdApp.getMetadata().setName(context.applicationInfo().getName());
        }

        if (clowdApp.getSpec() == null) {
            clowdApp.setSpec(new ClowdAppSpec());
        }
    }
}
