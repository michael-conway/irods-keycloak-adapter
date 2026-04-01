package org.example.keycloak.irods;

import org.junit.Test;
import static org.junit.Assert.*;

public class IrodsAuthResultTest extends AbstractIrodsTest {

    @Test
    public void testSuccess() {
        IrodsAuthResult result = IrodsAuthResult.success("user1");
        assertTrue(result.isAuthenticated());
        assertEquals("user1", result.getMappedKeycloakUsername());
        assertNull(result.getMessage());
    }

    @Test
    public void testFailure() {
        IrodsAuthResult result = IrodsAuthResult.failure("error");
        assertFalse(result.isAuthenticated());
        assertNull(result.getMappedKeycloakUsername());
        assertEquals("error", result.getMessage());
    }
}
