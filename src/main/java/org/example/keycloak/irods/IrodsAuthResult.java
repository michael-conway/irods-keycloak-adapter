package org.example.keycloak.irods;

public class IrodsAuthResult {
    private final boolean authenticated;
    private final String mappedKeycloakUsername;
    private final String message;

    private IrodsAuthResult(boolean authenticated, String mappedKeycloakUsername, String message) {
        this.authenticated = authenticated;
        this.mappedKeycloakUsername = mappedKeycloakUsername;
        this.message = message;
    }

    public static IrodsAuthResult success(String mappedKeycloakUsername) {
        return new IrodsAuthResult(true, mappedKeycloakUsername, null);
    }

    public static IrodsAuthResult failure() {
        return new IrodsAuthResult(false, null, null);
    }

    public static IrodsAuthResult failure(String message) {
        return new IrodsAuthResult(false, null, message);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getMappedKeycloakUsername() {
        return mappedKeycloakUsername;
    }

    public String getMessage() {
        return message;
    }
}
