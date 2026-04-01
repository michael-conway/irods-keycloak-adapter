# keycloak-irods-authenticator-spi

Standalone Maven project for a custom Keycloak Authenticator SPI.

This project is intentionally **not wired to iRODS yet**. It exists to give you a clean, buildable provider scaffold that can be integrated with a real iRODS authentication path in a second step.

## Included

- `IrodsAuthenticator`: Keycloak login-flow authenticator skeleton
- `IrodsAuthenticatorFactory`: provider factory registered through Java SPI
- `IrodsAuthService`: integration seam for later iRODS wiring
- `StubIrodsAuthService`: placeholder implementation that always fails closed
- `IrodsAuthResult`: small result object for auth outcomes
- `login-irods.ftl`: custom login form template
- `META-INF/services/org.keycloak.authentication.AuthenticatorFactory`: provider registration

## Design goal

This repository is meant to be:

- standalone,
- safe to load into Keycloak without external dependencies,
- ready to wire to iRODS later.

There is no embedded HTTP client, no iRODS Java client usage, no token exchange logic, and no environment-specific configuration.

## Build

```bash
mvn clean package
```

This produces a provider JAR under `target/`.

## Deploy to Keycloak

1. Copy the JAR to your Keycloak `providers/` directory.
2. Rebuild Keycloak:

```bash
bin/kc.sh build
```

3. Start Keycloak.
4. In the Admin Console, copy an authentication flow and add the **iRODS Authenticator** execution.

## Current behavior

- The login form renders.
- Submitted credentials are handed to `IrodsAuthService`.
- The default `StubIrodsAuthService` authenticates to the underlying iRODS service

That gives you a stable starting point before adding real iRODS integration.

## Testing notes

There is a test framework that supports unit tests in the compose directory.

* run docker compose build to build a test irods and keycloak server
* run docker compose up to launch with default testing configuration
* run the unit tests 


## Deployment notes

## Version notes

This skeleton targets Keycloak `26.5.5` and Java `17`.
If your Keycloak distribution differs, update `keycloak.version` in `pom.xml`.
