package org.example.keycloak.irods;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.UserModel;

/**
 * Standalone skeleton authenticator for an iRODS-specific login step.
 *
 * This project is intentionally unwired: it provides the Keycloak SPI surface,
 * form handling, and user mapping seam, while delegating iRODS-specific work to
 * {@link IrodsAuthService} for a later integration step.
 */
public class IrodsAuthenticator implements Authenticator {
    private static final Logger LOG = Logger.getLogger(IrodsAuthenticator.class);

    private final IrodsAuthService irodsAuthService;

    public IrodsAuthenticator(IrodsAuthService irodsAuthService) {
        this.irodsAuthService = irodsAuthService;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        Response challenge = context.form()
                .setAttribute("realm", context.getRealm())
                .createForm("login-irods.ftl");
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
        String username = formParams.getFirst("username");
        String password = formParams.getFirst("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            Response challenge = context.form()
                    .setError("Missing username or password")
                    .createForm("login-irods.ftl");
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }

        IrodsAuthResult result = irodsAuthService.authenticate(username, password, context);
        if (!result.isAuthenticated()) {
            LOG.debugf("iRODS authentication failed for user '%s'", username);
            String message = result.getMessage() == null ? "Invalid iRODS credentials" : result.getMessage();
            Response challenge = context.form()
                    .setError(message)
                    .createForm("login-irods.ftl");
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }

        UserModel user = context.getSession().users().getUserByUsername(
                context.getRealm(),
                result.getMappedKeycloakUsername()
        );
        if (user == null) {
            Response challenge = context.form()
                    .setError("User authenticated externally but is not provisioned in Keycloak")
                    .createForm("login-irods.ftl");
            context.failureChallenge(AuthenticationFlowError.UNKNOWN_USER, challenge);
            return;
        }

        context.setUser(user);
        context.success();
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(org.keycloak.models.KeycloakSession session,
                                 org.keycloak.models.RealmModel realm,
                                 UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(org.keycloak.models.KeycloakSession session,
                                   org.keycloak.models.RealmModel realm,
                                   UserModel user) {
        // no-op in standalone skeleton
    }

    @Override
    public void close() {
        // no-op in standalone skeleton
    }
}
