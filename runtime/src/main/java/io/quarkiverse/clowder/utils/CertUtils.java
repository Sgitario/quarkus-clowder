package io.quarkiverse.clowder.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.jboss.logging.Logger;

public final class CertUtils {

    public static final String CLOWDER_CERTIFICATE_STORE_TYPE = "PKCS12";
    private static final Logger LOG = Logger.getLogger(CertUtils.class.getName());

    private CertUtils() {

    }

    public static List<String> readCerts(String certString) {
        return Arrays.stream(certString.split("-----BEGIN CERTIFICATE-----"))
                .filter(s -> !s.isEmpty())
                .map(s -> Arrays.stream(s
                        .split("-----END CERTIFICATE-----"))
                        .filter(s2 -> !s2.isEmpty())
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Invalid certificate found"))

                )
                .map(String::trim)
                .map(s -> s.replaceAll("\n", ""))
                .toList();
    }

    public static List<byte[]> parsePemCert(List<String> base64Certs) {
        return base64Certs
                .stream()
                .map(cert -> Base64.getDecoder().decode(cert.getBytes(StandardCharsets.UTF_8)))
                .toList();
    }

    public static X509Certificate buildX509Cert(byte[] cert) {
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(cert));
        } catch (CertificateException certificateException) {
            throw new IllegalStateException("Couldn't load the x509 certificate factory", certificateException);
        }
    }

    public static String writeTruststore(KeyStore keyStore, char[] password) {
        try {
            File file = createTempFile("truststore", ".trust");
            keyStore.store(new FileOutputStream(file), password);
            return file.getAbsolutePath();
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException("Truststore creation failed", e);
        }
    }

    public static String createTempCertFile(String fileName, String certData) {
        byte[] cert = certData.getBytes(StandardCharsets.UTF_8);

        try {
            File certFile = createTempFile(fileName, ".crt");
            return Files.write(Path.of(certFile.getAbsolutePath()), cert).toString();
        } catch (IOException e) {
            throw new UncheckedIOException("Certificate file creation failed", e);
        }
    }

    public static File createTempFile(String fileName, String suffix) throws IOException {
        File file = File.createTempFile(fileName, suffix);

        try {
            file.deleteOnExit();
        } catch (SecurityException e) {
            LOG.warnf(e, "Delete on exit of the '%s' cert file denied by the security manager", fileName);
        }

        return file;
    }
}
