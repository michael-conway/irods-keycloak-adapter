package org.irods.keycloak.plugins;

import org.jboss.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

public class IrodsEventListenerConfigurationService {
    private static final Logger LOG = Logger.getLogger(IrodsEventListenerConfigurationService.class);

    private static final String DEFAULT_CONFIG_PATH = "/etc/irods-ext/authenticator.properties";
    private static final String CLASSPATH_CONFIG_PATH = "authenticator.properties";

    public IrodsEventListenerConfiguration initConfiguration() {
        IrodsEventListenerConfiguration config = initConfiguration(DEFAULT_CONFIG_PATH);
        if (!config.isEnabled()) {
            loadFromClasspath(config);
        }
        return config;
    }

    public IrodsEventListenerConfiguration initConfiguration(String path) {
        IrodsEventListenerConfiguration config = new IrodsEventListenerConfiguration();
        File configFile = new File(path);
        if (!configFile.exists()) {
            return config;
        }

        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
            applyProperties(config, properties);
        } catch (IOException e) {
            LOG.errorf(e, "Failed to load Keycloak event listener configuration from %s", path);
        }
        return config;
    }

    private void loadFromClasspath(IrodsEventListenerConfiguration config) {
        Properties properties = new Properties();
        try (var is = getClass().getClassLoader().getResourceAsStream(CLASSPATH_CONFIG_PATH)) {
            if (is != null) {
                properties.load(is);
                applyProperties(config, properties);
            }
        } catch (IOException e) {
            LOG.errorf(e, "Failed to load %s from classpath", CLASSPATH_CONFIG_PATH);
        }
    }

    private void applyProperties(IrodsEventListenerConfiguration config, Properties properties) {
        config.setAdminServiceEventUrl(stringProperty(properties, "keycloakEventAdminServiceUrl", config.getAdminServiceEventUrl()));
        config.setSharedSecret(secretProperty(properties, "keycloakEventSharedSecret", "keycloakEventSharedSecretFile", config.getSharedSecret()));
        config.setManagedRealm(stringProperty(properties, "keycloakEventManagedRealm", config.getManagedRealm()));
        config.setManagedGroupRoot(normalizeGroupRoot(stringProperty(properties, "keycloakEventManagedGroupRoot", config.getManagedGroupRoot())));
        config.setConnectTimeoutMillis(intProperty(properties, "keycloakEventConnectTimeoutMillis", config.getConnectTimeoutMillis()));
        config.setRequestTimeoutMillis(intProperty(properties, "keycloakEventRequestTimeoutMillis", config.getRequestTimeoutMillis()));
        config.setMaxRetries(Math.max(0, intProperty(properties, "keycloakEventMaxRetries", config.getMaxRetries())));
        config.setIrodsAdminZone(stringProperty(properties, "keycloakEventIrodsAdminZone", config.getIrodsAdminZone()));
        config.setIrodsAdminUsername(stringProperty(properties, "keycloakEventIrodsAdminUsername", config.getIrodsAdminUsername()));
        config.setIrodsAdminHost(stringProperty(properties, "keycloakEventIrodsAdminHost", config.getIrodsAdminHost()));
        config.setIrodsAdminPort(intProperty(properties, "keycloakEventIrodsAdminPort", config.getIrodsAdminPort()));
        config.setIrodsAdminResource(stringProperty(properties, "keycloakEventIrodsAdminResource", config.getIrodsAdminResource()));
        config.setIrodsAdminCredentialEncoding(stringProperty(properties, "keycloakEventIrodsAdminCredentialEncoding", config.getIrodsAdminCredentialEncoding()));
        config.setIrodsAdminCredentialValue(secretProperty(properties, "keycloakEventIrodsAdminCredentialValue", "keycloakEventIrodsAdminCredentialFile", config.getIrodsAdminCredentialValue()));
        config.setIrodsAdminCredentialKeyId(stringProperty(properties, "keycloakEventIrodsAdminCredentialKeyId", config.getIrodsAdminCredentialKeyId()));
    }

    private String stringProperty(Properties properties, String name, String fallback) {
        return properties.getProperty(name, fallback).trim();
    }

    private int intProperty(Properties properties, String name, int fallback) {
        String raw = properties.getProperty(name);
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            LOG.errorf("Invalid integer property %s: %s", name, raw);
            return fallback;
        }
    }

    private String secretProperty(Properties properties, String inlineName, String fileName, String fallback) {
        String filePath = properties.getProperty(fileName);
        if (filePath != null && !filePath.isBlank()) {
            try {
                return Files.readString(new File(filePath.trim()).toPath(), StandardCharsets.UTF_8).trim();
            } catch (IOException e) {
                LOG.errorf(e, "Failed to read secret file from property %s", fileName);
            }
        }
        return stringProperty(properties, inlineName, fallback);
    }

    private String normalizeGroupRoot(String groupRoot) {
        if (groupRoot == null || groupRoot.isBlank()) {
            return "";
        }
        return groupRoot.startsWith("/") ? groupRoot : "/" + groupRoot;
    }
}
