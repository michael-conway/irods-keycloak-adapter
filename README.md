# iRODS Keycloak Adapter

`irods-keycloak-adapter` is an alpha Keycloak Java provider project for
iRODS-oriented Keycloak integration.

It currently contains two Keycloak SPIs:

- a login-flow authenticator scaffold for iRODS-oriented authentication work,
- a thin admin-event listener that forwards selected Keycloak user, group, and
  group-membership activity to `irods-keycloak-admin`.

The adapter is intentionally not the iRODS administration layer.
`irods-keycloak-admin` owns iRODS mutation policy, mapping resolution, audit,
retry behavior, and the private callback API. This repository supplies the
Keycloak-side plugin surface needed to observe Keycloak activity and submit
compact callbacks to that Go service.

## Status

| Field | Value |
| --- | --- |
| Release | `0.1.0-SNAPSHOT` |
| Stability | Alpha / active development |
| Java | `17` |
| Keycloak | `26.5.5` |
| Maven group | `org.irods` |
| Package | `org.irods.keycloak.plugins` |

The event listener is disabled by default unless an admin-service callback URL
is configured.

## Purpose

This project exists to keep Keycloak-side integration narrow and reviewable.
It should load into Keycloak, observe selected administrative events, and call
the `irods-keycloak-admin` private event endpoint.

It is not intended to:

- replace iRODS ACL, user, group, ticket, collection, data object, resource, or
  metadata administration,
- embed broad iRODS administration logic inside the Keycloak JVM,
- maintain an independent synchronization database,
- act as a generic Keycloak webhook framework.

## Main Components

| Component | Purpose |
| --- | --- |
| `IrodsAuthenticator` | Keycloak login-flow authenticator scaffold. |
| `IrodsAuthenticatorFactory` | Authenticator provider factory registered through Java SPI. |
| `IrodsAuthService` | Integration seam for authentication wiring. |
| `StubIrodsAuthService` | Current iRODS native-auth service implementation used by the scaffold. |
| `IrodsKeycloakEventListenerProvider` | Thin admin-event listener for `irods-keycloak-admin` callbacks. |
| `IrodsKeycloakEventListenerProviderFactory` | Event-listener provider factory registered through Java SPI. |
| `src/main/resources/theme-resources/templates/login-irods.ftl` | Custom login form template. |
| `compose` | Local container/test setup files. |

## Quick Start

Build the provider JAR:

```bash
mvn clean package
```

The deployable assembly is written to:

```text
target/keycloak-irods-authenticator.jar
```

Install into Keycloak:

```bash
cp target/keycloak-irods-authenticator.jar "$KEYCLOAK_HOME/providers/"
"$KEYCLOAK_HOME/bin/kc.sh" build
```

Enable the authenticator by adding the **iRODS Authenticator** execution to a
copied authentication flow.

Enable admin-event forwarding by adding `irods-keycloak-admin-events` to the
realm event listener list and configuring the callback URL and shared secret.

## Runtime Model

The intended event flow is:

```text
Keycloak Admin Console activity
        |
        v
irods-keycloak-adapter EventListenerProvider
        |
        v
POST /admin/v1/keycloak/events on irods-keycloak-admin
        |
        v
iRODS mutation, audit, retry, and mapping policy in the Go service
```

The Java listener observes Keycloak admin events and sends compact callback
payloads. The Go service remains the system that decides what iRODS action, if
any, should occur.

## Documentation

- [Configuration Guide](./CONFIGURATION_GUIDE.md) - properties file format,
  listener enablement, callback authentication, and Keycloak setup.
- [Developer Notes](./DEVELOPER_NOTES.md) - architecture boundaries,
  implementation notes, build/test workflow, and current behavior.
- [`irods-keycloak-admin`](../irods-keycloak-admin/README.md) - related Go
  control-plane service and callback API owner.

## References

- [Keycloak Server Development guide](https://www.keycloak.org/docs/latest/server_development/)
- [irods-keycloak-admin](../irods-keycloak-admin/README.md)
- [irods4j](https://github.com/irods/irods4j)
