package org.example.keycloak.irods;

import org.jboss.logging.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.io.File;

/*
Service to derive an IrodsAuthenticatorConfiguration
 */
public class IrodsAuthenticatorConfigurationService {
    private static final Logger LOG = Logger.getLogger(IrodsAuthenticatorConfigurationService.class);
    
    private static final String DEFAULT_CONFIG_PATH = "/etc/irods-ext/authenticator.properties";
    private static final String CLASSPATH_CONFIG_PATH = "authenticator.properties";

    public IrodsAuthenticatorConfiguration initConfiguration() {
        IrodsAuthenticatorConfiguration config = initConfiguration(DEFAULT_CONFIG_PATH);

        // if values still defaults, try classpath
        if (config.getIrodsHost().isEmpty() && config.getIrodsZone().isEmpty()) {
            LOG.infof("No configuration found at %s, trying classpath: %s", DEFAULT_CONFIG_PATH, CLASSPATH_CONFIG_PATH);
            loadFromClasspath(config);
        }

        return config;
    }

    private void loadFromClasspath(IrodsAuthenticatorConfiguration config) {
        Properties properties = new Properties();
        try (var is = getClass().getClassLoader().getResourceAsStream(CLASSPATH_CONFIG_PATH)) {
            if (is != null) {
                LOG.infof("Loading configuration from classpath: %s", CLASSPATH_CONFIG_PATH);
                properties.load(is);
                applyProperties(config, properties);
            } else {
                LOG.infof("No %s found in classpath", CLASSPATH_CONFIG_PATH);
            }
        } catch (IOException e) {
            LOG.errorf(e, "Failed to load %s from classpath", CLASSPATH_CONFIG_PATH);
        }
    }

    private void applyProperties(IrodsAuthenticatorConfiguration config, Properties properties) {
        config.setIrodsHost(properties.getProperty("irodsHost", config.getIrodsHost()));
        String portString = properties.getProperty("irodsPort");
        if (portString != null) {
            try {
                config.setIrodsPort(Integer.parseInt(portString));
            } catch (NumberFormatException e) {
                LOG.errorf("Invalid irodsPort in properties: %s", portString);
            }
        }
        config.setIrodsZone(properties.getProperty("irodsZone", config.getIrodsZone()));
    }

    public IrodsAuthenticatorConfiguration initConfiguration(String path) {
        LOG.infof("initializing IrodsAuthenticatorConfiguration from path: %s", path);
        IrodsAuthenticatorConfiguration config = new IrodsAuthenticatorConfiguration();

        // look in given path
        File configFile = new File(path);
        if (configFile.exists()) {
            LOG.infof("Loading configuration from %s", configFile.getAbsolutePath());
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
                applyProperties(config, properties);
            } catch (IOException e) {
                LOG.errorf(e, "Failed to load %s", path);
            }
        }
        
        return config;
    }

}
