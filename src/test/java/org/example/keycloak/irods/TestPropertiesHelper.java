package org.example.keycloak.irods;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestPropertiesHelper {
    private static final String TESTING_PROPERTIES = "/testing.properties";
    private final Properties properties = new Properties();

    public TestPropertiesHelper() {
        try (InputStream input = getClass().getResourceAsStream(TESTING_PROPERTIES)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + TESTING_PROPERTIES);
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading " + TESTING_PROPERTIES, ex);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public Properties getProperties() {
        return properties;
    }
}
