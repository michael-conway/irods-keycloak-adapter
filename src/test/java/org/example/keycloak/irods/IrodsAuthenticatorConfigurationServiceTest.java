package org.example.keycloak.irods;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class IrodsAuthenticatorConfigurationServiceTest extends AbstractIrodsTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testInitConfigurationFromFile() throws IOException {
        File configDir = folder.newFolder("etc", "irods-ext");
        File configFile = new File(configDir, "authenticator.properties");
        
        Properties props = new Properties();
        props.setProperty("irodsHost", "testHost");
        props.setProperty("irodsPort", "1234");
        props.setProperty("irodsZone", "testZone");
        
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            props.store(out, null);
        }

        // We can now override the path for testing
        IrodsAuthenticatorConfigurationService service = new IrodsAuthenticatorConfigurationService();
        IrodsAuthenticatorConfiguration config = service.initConfiguration(configFile.getAbsolutePath());
        
        Assert.assertNotNull(config);
        Assert.assertEquals("testHost", config.getIrodsHost());
        Assert.assertEquals(1234, config.getIrodsPort());
        Assert.assertEquals("testZone", config.getIrodsZone());
    }

    @Test
    public void testInitConfigurationMissingFile() {
        IrodsAuthenticatorConfigurationService service = new IrodsAuthenticatorConfigurationService();
        IrodsAuthenticatorConfiguration config = service.initConfiguration("/non/existent/path");
        
        Assert.assertNotNull(config);
        // Defaults
        Assert.assertEquals("", config.getIrodsHost());
        Assert.assertEquals(1247, config.getIrodsPort());
        Assert.assertEquals("", config.getIrodsZone());
    }

    @Test
    public void testInitConfigurationFromClasspath() {
        // This will pick up src/test/resources/authenticator.properties
        IrodsAuthenticatorConfigurationService service = new IrodsAuthenticatorConfigurationService();
        IrodsAuthenticatorConfiguration config = service.initConfiguration();
        
        Assert.assertNotNull(config);
        Assert.assertEquals("localhost", config.getIrodsHost());
        Assert.assertEquals(1247, config.getIrodsPort());
        Assert.assertEquals("tempZone", config.getIrodsZone());
    }
}
