package io.quarkiverse.clowder.deployment.resources;

import java.util.ArrayList;
import java.util.Optional;

import io.fabric8.openshift.api.model.Parameter;
import io.fabric8.openshift.api.model.Template;

public abstract class ResourceDecorator {
    public abstract void decorate(Template template, ResourceDecoratorContext context);

    protected void setParameter(Template template, String name, String value) {
        if (template.getParameters() == null) {
            template.setParameters(new ArrayList<>());
        }

        var parameter = template.getParameters()
                .stream()
                .filter(p -> name.equals(p.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Parameter p = new Parameter();
                    p.setName(name);
                    template.getParameters().add(p);
                    return p;
                });

        parameter.setValue(value);
    }

    protected <T> Optional<T> findObject(Template template, Class<T> clazz) {
        return template.getObjects()
                .stream()
                .filter(clazz::isInstance)
                .map(m -> (T) m)
                .findFirst();
    }
}
