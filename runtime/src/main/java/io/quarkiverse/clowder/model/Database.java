package io.quarkiverse.clowder.model;

public class Database {

    private static final String SSL_MODE_DISABLE = "disable";
    private static final String VERIFY_FULL = "verify-full";

    public String adminPassword;
    public String adminUsername;
    public String hostname;
    public String name;
    public String password;
    public Integer port;
    public String sslMode;
    public String username;
    public String rdsCa;

    public boolean isSslModeEnabled() {
        return sslMode != null && !SSL_MODE_DISABLE.equals(sslMode);
    }

    public boolean isSslModeVerifyFull() {
        return VERIFY_FULL.equals(sslMode);
    }
}
