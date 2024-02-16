package io.quarkiverse.clowder.tests.minimal;

import static io.quarkiverse.clowder.test.utils.TemplateUtils.getParameterValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.redhat.cloud.v1alpha1.ClowdApp;
import com.redhat.cloud.v1alpha1.clowdappspec.Deployments;

import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.openshift.api.model.Template;

class ResourcesIT {
    @Test
    void shouldGenerateClowdAppResources() throws IOException {
        Template template = Serialization.unmarshal(getClowdAppResourceAsStream(), Template.class);
        assertNotNull(template, "Template is null!");
        Optional<ClowdApp> clowdApp = template.getObjects()
                .stream()
                .filter(ClowdApp.class::isInstance)
                .map(m -> (ClowdApp) m)
                .findFirst();
        assertTrue(clowdApp.isPresent());
        assertEquals("my-image", getParameterValue(template, "IMAGE"));
        Deployments deployment = clowdApp.get().getSpec().getDeployments().stream().findFirst().get();
        assertEquals("${IMAGE}", deployment.getPodSpec().getImage());
    }

    private InputStream getClowdAppResourceAsStream() throws FileNotFoundException {
        return new FileInputStream(Paths.get("target", "deploy", "clowdapp.yaml").toFile());
    }
}
