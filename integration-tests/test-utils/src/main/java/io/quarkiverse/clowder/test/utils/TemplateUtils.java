package io.quarkiverse.clowder.test.utils;

import io.fabric8.openshift.api.model.Parameter;
import io.fabric8.openshift.api.model.Template;

public class TemplateUtils {
    private TemplateUtils() {

    }

    public static String getParameterValue(Template template, String parameterName) {
        return template.getParameters().stream()
                .filter(p -> parameterName.equals(p.getName()))
                .map(Parameter::getValue)
                .findFirst()
                .orElse(null);
    }
}
