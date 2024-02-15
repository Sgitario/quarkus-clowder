package io.quarkiverse.clowder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import io.quarkiverse.clowder.model.Clowder;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class ClowderRecorder {
    public static Set<String> capabilities;
    public static Set<ClowderFeature> enabledFeatures;
    public static File clowderConfigFile;
    public static String clowderPrefix;
    public static Clowder model;

    public void initialize(Set<String> capabilities, Set<ClowderFeature> enabledFeatures, String prefix, String configFile) {
        ClowderRecorder.capabilities = capabilities;
        ClowderRecorder.enabledFeatures = enabledFeatures;
        ClowderRecorder.clowderPrefix = prefix;
        ClowderRecorder.clowderConfigFile = new File(configFile);
        ClowderRecorder.model = loadModel();
    }

    private Clowder loadModel() {
        var configFilePath = ClowderRecorder.clowderConfigFile.getAbsolutePath();
        if (!ClowderRecorder.clowderConfigFile.exists()) {
            throw new RuntimeException("Can't read clowder config at " + configFilePath);
        }

        try (InputStream is = new FileInputStream(clowderConfigFile)) {
            return yaml().load(is);
        } catch (IOException ex) {
            throw new RuntimeException("Error reading the clowder config file at " + configFilePath);
        }
    }

    private Yaml yaml() {
        Representer representer = new Representer(new DumperOptions());
        representer.getPropertyUtils().setSkipMissingProperties(true);
        LoaderOptions loaderOptions = new LoaderOptions();
        return new Yaml(new Constructor(Clowder.class, loaderOptions), representer);
    }
}
