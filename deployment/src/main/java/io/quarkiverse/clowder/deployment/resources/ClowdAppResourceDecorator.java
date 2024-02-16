package io.quarkiverse.clowder.deployment.resources;

import com.redhat.cloud.v1alpha1.ClowdApp;

import io.fabric8.openshift.api.model.Template;

public abstract class ClowdAppResourceDecorator extends ResourceDecorator {

    public abstract void decorate(ClowdApp clowdApp, Template template, ResourceDecoratorContext context);

    @Override
    public void decorate(Template template, ResourceDecoratorContext context) {
        ClowdApp clowdApp = findObject(template, ClowdApp.class)
                .orElseGet(() -> {
                    ClowdApp app = new ClowdApp();
                    template.getObjects().add(app);
                    return app;
                });
        decorate(clowdApp, template, context);
    }
}
