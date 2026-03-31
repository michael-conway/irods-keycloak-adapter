package org.example.keycloak.irods;

import org.junit.Test;
import static org.junit.Assert.*;

public class StubIrodsAuthServiceTest {

    @Test
    public void testAuthenticate_ReturnsFailure() {
        StubIrodsAuthService service = new StubIrodsAuthService();
        IrodsAuthResult result = service.authenticate("user", "pass", "native", null);
        assertFalse(result.isAuthenticated());
        assertEquals("No iRODS adapter is wired yet", result.getMessage());
    }
}
