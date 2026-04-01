package org.example.keycloak.irods;

import org.keycloak.authentication.AuthenticationFlowContext;

/**
 * Abstraction for iRODS authentication.
 *
 * This project intentionally does not provide a concrete iRODS implementation.
 * Wire a real adapter in a later step.
 */
public interface IrodsAuthService {
    IrodsAuthResult authenticate(String username, String password, String loginType, AuthenticationFlowContext context);
}
