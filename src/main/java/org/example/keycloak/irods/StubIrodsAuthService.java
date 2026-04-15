package org.example.keycloak.irods;

import org.irods.irods4j.authentication.NativeAuthPlugin;
import org.irods.irods4j.high_level.connection.IRODSConnection;
import org.irods.irods4j.high_level.connection.QualifiedUsername;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;

import java.io.IOException;

/**
 * Standalone placeholder implementation.
 *
 * This class exists only so the SPI can be packaged and loaded before any
 * iRODS-specific wiring is added. It always rejects authentication.
 */
public class StubIrodsAuthService implements IrodsAuthService {

    private IrodsAuthenticatorConfiguration irodsAuthenticatorConfiguration;
    private static final Logger LOG = Logger.getLogger(StubIrodsAuthService.class);


    public StubIrodsAuthService(final IrodsAuthenticatorConfiguration irodsAuthenticatorConfiguration) {
        if (irodsAuthenticatorConfiguration == null) {
            throw new IllegalArgumentException("null ironsAuthenticatorConfiguration");
        }
        this.irodsAuthenticatorConfiguration = irodsAuthenticatorConfiguration;
    }

    @Override
    public IrodsAuthResult authenticate(String username, String password, String loginType, AuthenticationFlowContext context) {
        LOG.info("authenticate()");

        if (username == null || username.isEmpty()) {

            throw new IllegalArgumentException("null or empty username");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("null or empty password");
        }

        if (loginType == null || loginType.isEmpty()) {
            throw new IllegalArgumentException("null or empty loginType");
        }

        IRODSConnection conn = new IRODSConnection();

        try {

            conn.connect(irodsAuthenticatorConfiguration.getIrodsHost(), irodsAuthenticatorConfiguration.getIrodsPort(),
                    new QualifiedUsername(username, irodsAuthenticatorConfiguration.getIrodsZone()));

            if (loginType.equals("native")) {
                conn.authenticate(new NativeAuthPlugin(), password);
            } else {
                throw new UnsupportedOperationException("Unsupported login type: " + loginType);
            }

            return IrodsAuthResult.success(username);

        } catch (Throwable t) {
            // irods4j can throw ExceptionInInitializerError from static logger init
            // under containerized runtimes; convert to auth failure instead of 500.
            LOG.warn("error authenticating user", t);
            String message = t.getMessage();
            if (message == null || message.isBlank()) {
                message = t.getClass().getSimpleName();
            }
            return IrodsAuthResult.failure(message);
        } finally {
            try {
                conn.disconnect();
            } catch (IOException e) {
                // ignore
            }

        }






    }
}
