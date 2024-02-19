package io.quarkiverse.clowder;

import static io.quarkiverse.clowder.utils.CertUtils.CLOWDER_CERTIFICATE_STORE_TYPE;
import static io.quarkiverse.clowder.utils.CertUtils.parsePemCert;
import static io.quarkiverse.clowder.utils.CertUtils.readCerts;
import static io.quarkiverse.clowder.utils.CertUtils.writeTruststore;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import io.quarkiverse.clowder.config.RootConfig;
import io.quarkiverse.clowder.model.Clowder;
import io.quarkiverse.clowder.utils.CertUtils;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class ClowderRecorder {

    private static final int DEFAULT_PASSWORD_LENGTH = 33;

    public static Set<String> capabilities;
    public static RootConfig rootConfig;
    public static Clowder model;
    public static String trustStorePath;
    public static String trustStorePassword;

    public void initialize(Set<String> capabilities, RootConfig rootConfig) {
        ClowderRecorder.capabilities = capabilities;
        ClowderRecorder.rootConfig = rootConfig;
        ClowderRecorder.model = loadModel();
    }

    public static String getTrustStorePath() {
        if (trustStorePath == null) {
            initializeTrustStoreCertificate();
        }

        return trustStorePath;
    }

    public static String getTrustStorePassword() {
        if (trustStorePassword == null) {
            initializeTrustStoreCertificate();
        }

        return trustStorePassword;
    }

    public static String getTrustStoreType() {
        return CLOWDER_CERTIFICATE_STORE_TYPE;
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

    private static void initializeTrustStoreCertificate() {
        if (model.tlsCAPath == null || model.tlsCAPath.isBlank()) {
            throw new IllegalStateException("Requested tls port for endpoint but did not provide tlsCAPath");
        }

        try {
            String certContent = Files.readString(new File(model.tlsCAPath).toPath(), UTF_8);
            List<String> base64Certs = readCerts(certContent);

            List<X509Certificate> certificates = parsePemCert(base64Certs)
                    .stream()
                    .map(CertUtils::buildX509Cert)
                    .toList();

            if (certificates.isEmpty()) {
                throw new IllegalStateException("Could not parse any certificate in the file");
            }

            // Generate a keystore by hand
            // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/security/KeyStore.html

            KeyStore truststore = KeyStore.getInstance(CLOWDER_CERTIFICATE_STORE_TYPE);

            // Per the docs, we need to init the new keystore with load(null)
            truststore.load(null);

            for (int i = 0; i < certificates.size(); i++) {
                truststore.setCertificateEntry("cert-" + i, certificates.get(i));
            }

            char[] password = buildPassword(base64Certs.get(0));
            ClowderRecorder.trustStorePath = writeTruststore(truststore, password);
            ClowderRecorder.trustStorePassword = new String(password);
        } catch (IOException ioe) {
            throw new IllegalStateException("Couldn't load the certificate, but we were requested a truststore", ioe);
        } catch (KeyStoreException kse) {
            throw new IllegalStateException("Couldn't load the keystore format PKCS12", kse);
        } catch (NoSuchAlgorithmException | CertificateException ce) {
            throw new IllegalStateException("Couldn't configure the keystore", ce);
        }
    }

    private static char[] buildPassword(String seed) {
        // To avoid having a fixed password - fetch the first characters from a string (the certificate)
        int size = Math.min(DEFAULT_PASSWORD_LENGTH, seed.length());
        char[] password = new char[size];
        seed.getChars(0, size, password, 0);
        return password;
    }
}
