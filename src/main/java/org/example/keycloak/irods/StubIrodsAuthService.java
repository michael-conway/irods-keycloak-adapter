package org.example.keycloak.irods;

import org.keycloak.authentication.AuthenticationFlowContext;

/**
 * Standalone placeholder implementation.
 *
 * This class exists only so the SPI can be packaged and loaded before any
 * iRODS-specific wiring is added. It always rejects authentication.
 */
public class StubIrodsAuthService implements IrodsAuthService {
    @Override
    public IrodsAuthResult authenticate(String username, String password, String loginType, AuthenticationFlowContext context) {
        return IrodsAuthResult.failure("No iRODS adapter is wired yet");
    }
}
