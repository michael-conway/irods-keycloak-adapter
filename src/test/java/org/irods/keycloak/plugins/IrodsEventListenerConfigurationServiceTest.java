package org.irods.keycloak.plugins;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

public class IrodsEventListenerConfigurationServiceTest extends AbstractIrodsTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testInitConfigurationFromFile() throws IOException {
        File configFile = folder.newFile("authenticator.properties");
        File sharedSecretFile = folder.newFile("event-secret");
        File credentialFile = folder.newFile("irods-admin-credential");
        Files.writeString(sharedSecretFile.toPath(), "shared-secret\n", StandardCharsets.UTF_8);
        Files.writeString(credentialFile.toPath(), "encoded-credential\n", StandardCharsets.UTF_8);

        Properties props = new Properties();
        props.setProperty("keycloakEventAdminServiceUrl", "http://localhost:8081/admin/v1/keycloak/events");
        props.setProperty("keycloakEventSharedSecretFile", sharedSecretFile.getAbsolutePath());
        props.setProperty("keycloakEventManagedRealm", "irods");
        props.setProperty("keycloakEventManagedGroupRoot", "irods");
        props.setProperty("keycloakEventConnectTimeoutMillis", "100");
        props.setProperty("keycloakEventRequestTimeoutMillis", "200");
        props.setProperty("keycloakEventMaxRetries", "3");
        props.setProperty("keycloakEventIrodsAdminZone", "tempZone");
        props.setProperty("keycloakEventIrodsAdminUsername", "rods");
        props.setProperty("keycloakEventIrodsAdminHost", "127.0.0.1");
        props.setProperty("keycloakEventIrodsAdminPort", "1247");
        props.setProperty("keycloakEventIrodsAdminResource", "providerResc");
        props.setProperty("keycloakEventIrodsAdminCredentialFile", credentialFile.getAbsolutePath());
        props.setProperty("keycloakEventIrodsAdminCredentialKeyId", "local");

        try (FileOutputStream out = new FileOutputStream(configFile)) {
            props.store(out, null);
        }

        IrodsEventListenerConfiguration config =
                new IrodsEventListenerConfigurationService().initConfiguration(configFile.getAbsolutePath());

        Assert.assertTrue(config.isEnabled());
        Assert.assertEquals("http://localhost:8081/admin/v1/keycloak/events", config.getAdminServiceEventUrl());
        Assert.assertEquals("shared-secret", config.getSharedSecret());
        Assert.assertEquals("irods", config.getManagedRealm());
        Assert.assertEquals("/irods", config.getManagedGroupRoot());
        Assert.assertEquals(100, config.getConnectTimeoutMillis());
        Assert.assertEquals(200, config.getRequestTimeoutMillis());
        Assert.assertEquals(3, config.getMaxRetries());
        Assert.assertEquals("tempZone", config.getIrodsAdminZone());
        Assert.assertEquals("rods", config.getIrodsAdminUsername());
        Assert.assertEquals("127.0.0.1", config.getIrodsAdminHost());
        Assert.assertEquals(1247, config.getIrodsAdminPort());
        Assert.assertEquals("providerResc", config.getIrodsAdminResource());
        Assert.assertEquals("encoded-credential", config.getIrodsAdminCredentialValue());
        Assert.assertEquals("local", config.getIrodsAdminCredentialKeyId());
    }

    @Test
    public void testMissingConfigurationIsDisabled() {
        IrodsEventListenerConfiguration config =
                new IrodsEventListenerConfigurationService().initConfiguration("/non/existent/path");

        Assert.assertFalse(config.isEnabled());
        Assert.assertEquals("", config.getAdminServiceEventUrl());
    }
}
