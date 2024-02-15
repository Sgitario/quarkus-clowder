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

import io.quarkiverse.clowder.config.RootConfig;
import io.quarkiverse.clowder.model.Clowder;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class ClowderRecorder {
    public static Set<String> capabilities;
    public static RootConfig rootConfig;
    public static Clowder model;

    public void initialize(Set<String> capabilities, RootConfig rootConfig) {
        ClowderRecorder.capabilities = capabilities;
        ClowderRecorder.rootConfig = rootConfig;
        ClowderRecorder.model = loadModel();
    }

    private Clowder loadModel() {
        var configFile = new File(ClowderRecorder.rootConfig.configPath);
        var configFilePath = configFile.getAbsolutePath();
        if (!configFile.exists()) {
            throw new RuntimeException("Can't read clowder config at " + configFilePath);
        }

        try (InputStream is = new FileInputStream(configFile)) {
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
