package unex;

import java.io.InputStream;
import java.util.Properties;

public class MongodbConfig {
    private static final String CONFIG_FILE = "mongodb-config.properties";

    private Properties properties;

    public MongodbConfig() {
        this.properties = loadProperties();
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                props.load(input);
            } else {
                throw new RuntimeException("Unable to find " + CONFIG_FILE);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading configuration from " + CONFIG_FILE, e);
        }
        return props;
    }

    public String getHost() {
        return properties.getProperty("mongodb.host");
    }

    public int getPort() {
        return Integer.parseInt(properties.getProperty("mongodb.port"));
    }

    public String getUsername() {
        return properties.getProperty("mongodb.username");
    }

    public String getPassword() {
        return properties.getProperty("mongodb.password");
    }

    public String getDatabase() {
        return properties.getProperty("mongodb.database");
    }


}
