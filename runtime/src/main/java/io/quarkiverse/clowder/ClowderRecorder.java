package io.quarkiverse.clowder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import io.quarkiverse.clowder.model.Clowder;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class ClowderRecorder {

    public static File clowderConfigFile;
    public static String clowderPrefix;
    public static Clowder model;

    public void loadClowder(String prefix, String configFile) {
        clowderPrefix = prefix;
        clowderConfigFile = new File(configFile);
        if (!clowderConfigFile.exists()) {
            throw new RuntimeException("Can't read clowder config at " + clowderConfigFile.getAbsolutePath());
        }

        try (InputStream is = new FileInputStream(clowderConfigFile)) {
            model = yaml().load(is);
        } catch (IOException ex) {
            throw new RuntimeException("Error reading the clowder config file at " + configFile);
        }
    }

    private Yaml yaml() {
        Representer representer = new Representer(new DumperOptions());
        representer.getPropertyUtils().setSkipMissingProperties(true);
        LoaderOptions loaderOptions = new LoaderOptions();
        return new Yaml(new Constructor(Clowder.class, loaderOptions), representer);
    }
}
