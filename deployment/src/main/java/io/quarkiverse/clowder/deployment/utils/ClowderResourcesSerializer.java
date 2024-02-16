package io.quarkiverse.clowder.deployment.utils;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_EMPTY_JSON_ARRAYS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_NULL_MAP_VALUES;
import static com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS;
import static com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR;
import static com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature.MINIMIZE_QUOTES;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import io.fabric8.openshift.api.model.Template;

public class ClowderResourcesSerializer {

    private static final ObjectMapper YAML_MAPPER = createYamlMapper(
            new YAMLGenerator.Feature[] { MINIMIZE_QUOTES, ALWAYS_QUOTE_NUMBERS_AS_STRINGS, INDENT_ARRAYS_WITH_INDICATOR },
            new SerializationFeature[] { INDENT_OUTPUT },
            new SerializationFeature[] { WRITE_NULL_MAP_VALUES, WRITE_EMPTY_JSON_ARRAYS });

    private static final String START_EXPRESSION = "${";
    private static final String END_EXPRESSION = "}";
    private static final Pattern EXPRESSIONS = Pattern.compile("\"\\$\\{(.+?)\\}\"");

    private ClowderResourcesSerializer() {

    }

    public static Template read(Path inputFilePath) {
        File inputFile = inputFilePath.toFile();
        if (!inputFile.exists()) {
            return new Template();
        }

        try (InputStream is = new FileInputStream(inputFile)) {
            return YAML_MAPPER.readValue(is, Template.class);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to read kubernetes resources from: " + inputFile, e);
        }
    }

    public static String write(Path outputFilePath, Template template) {

        try {
            String yamlValue = asYaml(template);
            outputFilePath.getParent().normalize().toFile().mkdirs();
            try (FileWriter writer = new FileWriter(outputFilePath.toFile())) {
                writer.write(yamlValue);
                // replace expressions
                var matcher = EXPRESSIONS.matcher(yamlValue);
                while (matcher.find()) {
                    var match = matcher.group();
                    var inner = matcher.group(1);
                    yamlValue = yamlValue.replace(match, START_EXPRESSION + inner + END_EXPRESSION);
                }

                return yamlValue;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing resources", e);
        }
    }

    private static YAMLFactory createYamlFactory(YAMLGenerator.Feature[] features) {
        YAMLFactory result = new YAMLFactory();
        for (YAMLGenerator.Feature feature : features) {
            result = result.enable(feature);
        }
        return result;
    }

    private static ObjectMapper createYamlMapper(YAMLGenerator.Feature[] generatorFeatures,
            SerializationFeature[] enabledFeatures, SerializationFeature[] disabledFeatures) {
        var objectMapper = new ObjectMapper(createYamlFactory(generatorFeatures));
        for (SerializationFeature feature : enabledFeatures) {
            objectMapper.configure(feature, true);
        }
        for (SerializationFeature feature : disabledFeatures) {
            objectMapper.configure(feature, false);
        }

        return objectMapper;
    }

    private static <T> String asYaml(T object) {
        try {
            return YAML_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
