package io.quarkiverse.clowder.deployment.resources;

import java.util.HashMap;

import com.redhat.cloud.v1alpha1.ClowdApp;

import io.fabric8.openshift.api.model.Template;

public class LabelsClowdAppResourceDecorator extends ClowdAppResourceDecorator {
    @Override
    public void decorate(ClowdApp clowdApp, Template template, ResourceDecoratorContext context) {
        var labels = context.config().resources().labels();
        if (labels.isEmpty()) {
            return;
        }

        if (clowdApp.getMetadata().getLabels() == null) {
            clowdApp.getMetadata().setLabels(new HashMap<>());
        }

        clowdApp.getMetadata().getLabels().putAll(labels);
    }
}
