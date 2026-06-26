# Developer Notes

`irods-keycloak-adapter` is the Keycloak-side plugin project for the
`irods-keycloak-admin` control plane. The repository should stay small and
conservative: Keycloak observes or authenticates, while the Go service owns
iRODS administration behavior.

## Repository Boundary

The adapter may:

- implement Keycloak Java SPIs,
- render Keycloak login-flow UI,
- validate local plugin configuration,
- normalize selected Keycloak admin-event context,
- submit private callbacks to `irods-keycloak-admin`.

The adapter should not:

- implement broad iRODS provisioning policy,
- decide destructive deprovisioning behavior,
- maintain a separate synchronization database,
- duplicate `irods-keycloak-admin` mapping or audit logic,
- become a generic iRODS REST API or a generic Keycloak webhook framework.

## Relation to irods-keycloak-admin

`irods-keycloak-admin` exposes the private event endpoint:

```http
POST /admin/v1/keycloak/events
X-IRODS-KC-Shared-Secret: ...
Content-Type: application/json
```

The Java listener builds the callback request and sends it with a tight
timeout. The Go service validates the shared secret, owns idempotency handling,
resolves mapping policy, mutates iRODS, records audit state, and controls retry
or repair workflows.

## Main Java Providers

| Provider | Keycloak SPI | Provider ID |
| --- | --- | --- |
| `IrodsAuthenticatorFactory` | `AuthenticatorFactory` | `irods-authenticator` |
| `IrodsKeycloakEventListenerProviderFactory` | `EventListenerProviderFactory` | `irods-keycloak-admin-events` |

Service-loader registrations:

- `src/main/resources/META-INF/services/org.keycloak.authentication.AuthenticatorFactory`
- `src/main/resources/META-INF/services/org.keycloak.events.EventListenerProviderFactory`

## Event Listener Behavior

The event listener ignores login events and only handles selected admin events.

Supported resource types:

- `USER`
- `GROUP`
- `GROUP_MEMBERSHIP`

The listener skips failed admin events and can filter by managed realm and
managed group root. For accepted events, it builds a
`KeycloakEventRequest`-shaped JSON payload for `irods-keycloak-admin`.

Representative event flow:

```text
Admin creates, updates, or deletes a managed user/group/membership.
Keycloak invokes EventListenerProvider.
The listener filters unsupported or unmanaged events.
The listener posts a compact callback payload to irods-keycloak-admin.
irods-keycloak-admin decides and performs any iRODS-side mutation.
```

## Authenticator Behavior

The login-flow authenticator renders `login-irods.ftl`, accepts submitted
credentials, and delegates authentication to `IrodsAuthService`.

The current `StubIrodsAuthService` uses `irods4j` native authentication against
the configured iRODS host, port, and zone. Failed iRODS authentication is
converted into a normal authentication failure rather than a Keycloak server
error.

## Build

```bash
mvn clean package
```

The deployable provider assembly is:

```text
target/keycloak-irods-authenticator.jar
```

The Maven coordinate is currently:

```text
org.irods:keycloak-irods-authenticator-spi:0.1.0-SNAPSHOT
```

## Tests

Run focused event-listener tests:

```bash
mvn -Dtest=IrodsEventListenerConfigurationServiceTest,IrodsKeycloakEventRequestBuilderTest test
```

Run the full test suite:

```bash
mvn test
```

The full suite includes `StubIrodsAuthServiceTest`, which attempts real iRODS
native authentication using `src/test/resources/testing.properties`. That test
requires a reachable test iRODS service with matching credentials.

## Packaging Notes

After changing Java package names or service-loader entries, use a clean build
before inspecting the artifact:

```bash
mvn clean -DskipTests package
jar tf target/keycloak-irods-authenticator.jar
```

This avoids stale class-directory entries from previous package layouts.

## Version Notes

The project targets:

- Java `17`
- Keycloak `26.5.5`
- package namespace `org.irods.keycloak.plugins`

If the Keycloak distribution changes, update `keycloak.version` in `pom.xml`
and verify the SPI signatures still match.
