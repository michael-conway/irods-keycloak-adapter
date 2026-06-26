# Configuration Guide

`irods-keycloak-adapter` reads runtime settings from
`authenticator.properties`.

The primary filesystem location is:

```text
/etc/irods-ext/authenticator.properties
```

If that file is not present, the plugin falls back to the classpath
`authenticator.properties` bundled in the provider JAR.

## Loading Model

1. Built-in defaults are applied.
2. `/etc/irods-ext/authenticator.properties` is loaded if present.
3. If the event listener remains disabled, classpath `authenticator.properties`
   is checked.
4. Secret-file properties are resolved where supported.

The event listener is disabled unless `keycloakEventAdminServiceUrl` is set.

## Authenticator Settings

```properties
irodsHost=localhost
irodsPort=1247
irodsZone=tempZone
```

| Property | Purpose |
| --- | --- |
| `irodsHost` | iRODS host used by the login authenticator service. |
| `irodsPort` | iRODS port. |
| `irodsZone` | iRODS zone for qualified usernames. |

## Event Listener Settings

```properties
keycloakEventAdminServiceUrl=http://irods-keycloak-admin:8081/admin/v1/keycloak/events
keycloakEventSharedSecretFile=/run/secrets/irods-kc-event-shared-secret
keycloakEventManagedRealm=irods
keycloakEventManagedGroupRoot=/irods
keycloakEventConnectTimeoutMillis=2000
keycloakEventRequestTimeoutMillis=5000
keycloakEventMaxRetries=2
keycloakEventIrodsAdminZone=tempZone
keycloakEventIrodsAdminUsername=rods
keycloakEventIrodsAdminHost=127.0.0.1
keycloakEventIrodsAdminPort=1247
keycloakEventIrodsAdminResource=providerResc
keycloakEventIrodsAdminCredentialFile=/run/secrets/irods-admin-credential-envelope
keycloakEventIrodsAdminCredentialEncoding=base64
keycloakEventIrodsAdminCredentialKeyId=local
```

| Property | Purpose |
| --- | --- |
| `keycloakEventAdminServiceUrl` | Full callback URL for `irods-keycloak-admin`; enables the listener when non-empty. |
| `keycloakEventSharedSecret` | Inline shared secret for `X-IRODS-KC-Shared-Secret`. Prefer the file form outside disposable local tests. |
| `keycloakEventSharedSecretFile` | File containing the shared secret. |
| `keycloakEventManagedRealm` | Realm filter for emitted events. Empty means all realms. |
| `keycloakEventManagedGroupRoot` | Group-root filter for group and membership events. Empty means all groups. |
| `keycloakEventConnectTimeoutMillis` | HTTP connection timeout for callbacks. |
| `keycloakEventRequestTimeoutMillis` | End-to-end HTTP request timeout for callbacks. |
| `keycloakEventMaxRetries` | Retry count for network errors and HTTP `5xx` responses. |
| `keycloakEventIrodsAdminZone` | iRODS admin zone included in the callback envelope. |
| `keycloakEventIrodsAdminUsername` | iRODS admin username included in the callback envelope. |
| `keycloakEventIrodsAdminHost` | iRODS host included in the callback envelope. |
| `keycloakEventIrodsAdminPort` | iRODS port included in the callback envelope. |
| `keycloakEventIrodsAdminResource` | Default iRODS resource included in the callback envelope. |
| `keycloakEventIrodsAdminCredentialEncoding` | Encoding label for the credential envelope, for example `base64`. |
| `keycloakEventIrodsAdminCredentialValue` | Inline credential envelope. Prefer the file form outside disposable local tests. |
| `keycloakEventIrodsAdminCredentialFile` | File containing the credential envelope. |
| `keycloakEventIrodsAdminCredentialKeyId` | Optional key identifier for credential rotation or encrypted envelopes. |

## Callback Authentication

The listener sends:

```http
X-IRODS-KC-Shared-Secret: <configured secret>
```

This value must match `KeycloakEventSharedSecret` or
`KeycloakEventSharedSecretFile` in `irods-keycloak-admin`.

The shared-secret model is an initial private-service trust boundary. Shared or
persistent deployments should prefer mounted secret files and should plan for
stronger hardening such as mTLS, signed event bodies, replay windows, key
rotation, or service-account tokens.

## Keycloak Setup

Build and copy the provider JAR into Keycloak:

```bash
mvn clean package
cp target/keycloak-irods-authenticator.jar "$KEYCLOAK_HOME/providers/"
"$KEYCLOAK_HOME/bin/kc.sh" build
```

Enable the login authenticator:

1. Open the Keycloak Admin Console.
2. Copy the target authentication flow.
3. Add the **iRODS Authenticator** execution.
4. Set the execution requirement appropriate for the deployment.

Enable admin-event forwarding:

1. Open the target realm.
2. Add `irods-keycloak-admin-events` to the realm event listener list.
3. Ensure `keycloakEventAdminServiceUrl` points to the reachable
   `irods-keycloak-admin` event endpoint.
4. Ensure the adapter shared secret matches the Go service configuration.

## Local Compose Notes

`compose/authenticator.properties` is a sample for local container use. The
event listener remains disabled until `keycloakEventAdminServiceUrl` is set.

Use direct inline secrets only for disposable local environments. For any
shared environment, use:

```properties
keycloakEventSharedSecretFile=/run/secrets/irods-kc-event-shared-secret
keycloakEventIrodsAdminCredentialFile=/run/secrets/irods-admin-credential-envelope
```
