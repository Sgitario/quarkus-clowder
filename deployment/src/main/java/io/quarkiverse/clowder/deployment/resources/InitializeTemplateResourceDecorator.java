package io.quarkiverse.clowder.deployment.resources;

import java.util.ArrayList;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.openshift.api.model.Template;

public class InitializeTemplateResourceDecorator extends ResourceDecorator {
    @Override
    public void decorate(Template template, ResourceDecoratorContext context) {
        if (template.getMetadata() == null) {
            template.setMetadata(new ObjectMeta());
        }

        if (template.getMetadata().getName() == null) {
            template.getMetadata().setName(context.applicationInfo().getName());
        }

        if (template.getObjects() == null) {
            template.setObjects(new ArrayList<>());
        }
    }
}
