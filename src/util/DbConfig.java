package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * Loads database configuration from {@code resources/db.properties}.
 */
public class DbConfig {

    private static final String DB_PROPERTIES_PATH = "resources/db.properties";

    private final String url;
    private final String username;
    private final String password;

    public DbConfig() {
        Properties props = new Properties();

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(DB_PROPERTIES_PATH)) {
            if (in == null) {
                throw new IllegalStateException(DB_PROPERTIES_PATH + " not found on the classpath.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + DB_PROPERTIES_PATH + ".", e);
        }

        this.url = requireProperty(props, "db.url");
        this.username = requireProperty(props, "db.username");
        this.password = requireProperty(props, "db.password");
    }

    private String requireProperty(Properties props, String key) {
        return Objects.requireNonNull(props.getProperty(key), key + " is required").trim();
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
