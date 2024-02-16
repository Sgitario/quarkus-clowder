package io.quarkiverse.clowder.tests.resources;

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
    void shouldContainExpectedMetadata() throws IOException {
        Template template = Serialization.unmarshal(getClowdAppResourceAsStream(), Template.class);
        assertNotNull(template, "Template is null!");
        Optional<ClowdApp> clowdAppOptional = template.getObjects()
                .stream()
                .filter(ClowdApp.class::isInstance)
                .map(m -> (ClowdApp) m)
                .findFirst();
        assertTrue(clowdAppOptional.isPresent());
        ClowdApp clowdApp = clowdAppOptional.get();
        assertEquals("test-environment", getParameterValue(template, "ENV_NAME"));
        assertEquals("${ENV_NAME}", clowdApp.getSpec().getEnvName());
        assertEquals("label-value-a", clowdApp.getMetadata().getLabels().get("label-a"));
        assertTrue(clowdApp.getSpec().getDeployments().stream().findFirst().isPresent());
        Deployments deployment = clowdApp.getSpec().getDeployments().stream().findFirst().get();
        assertEquals("service", deployment.getName());
        assertTrue(deployment.getWebServices().get_public().getEnabled());
        assertEquals("quay.io/user/my-image", getParameterValue(template, "IMAGE"));
        assertEquals("latest", getParameterValue(template, "IMAGE_TAG"));
        assertEquals("${IMAGE}:${IMAGE_TAG}", deployment.getPodSpec().getImage());
    }

    private InputStream getClowdAppResourceAsStream() throws FileNotFoundException {
        return new FileInputStream(Paths.get("target", "deploy", "clowdapp.yaml").toFile());
    }
}
