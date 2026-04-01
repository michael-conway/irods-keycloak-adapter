package org.example.keycloak.irods;

import org.junit.Test;
import static org.junit.Assert.*;

public class StubIrodsAuthServiceTest extends AbstractIrodsTest {

    @Test
    public void testAuthenticate() {
        String testUser = testProperties.getProperty("test_user_1");
        String testPassword = testProperties.getProperty("test_user_1_password");

        IrodsAuthenticatorConfigurationService configService = new IrodsAuthenticatorConfigurationService();
        IrodsAuthenticatorConfiguration config = configService.initConfiguration();

        StubIrodsAuthService service = new StubIrodsAuthService(config);
        IrodsAuthResult result = service.authenticate(testUser, testPassword, "native", null);
        assertTrue(result.isAuthenticated());
    }

    @Test
    public void testAuthenticateWhenInvalid() {
        String testUser = testProperties.getProperty("test_user_1");
        String testPassword = "clearlyinvalidpassword";

        IrodsAuthenticatorConfigurationService configService = new IrodsAuthenticatorConfigurationService();
        IrodsAuthenticatorConfiguration config = configService.initConfiguration();

        StubIrodsAuthService service = new StubIrodsAuthService(config);
        IrodsAuthResult result = service.authenticate(testUser, testPassword, "native", null);
        assertFalse(result.isAuthenticated());
    }
}
