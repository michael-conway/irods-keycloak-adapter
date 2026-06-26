package org.irods.keycloak.plugins;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.AuthDetails;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class IrodsKeycloakEventRequestBuilder {
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final IrodsEventListenerConfiguration config;
    private final ObjectMapper objectMapper;

    public IrodsKeycloakEventRequestBuilder(IrodsEventListenerConfiguration config, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> build(AdminEvent event) {
        String eventId = event.getId();
        if (eventId == null || eventId.isBlank()) {
            eventId = derivedEventId(event);
        }

        String operationType = value(event.getOperationType());
        String resourceType = resourceType(event);
        String eventType = "keycloak.admin."
                + resourceType.toLowerCase(Locale.ROOT)
                + "."
                + operationType.toLowerCase(Locale.ROOT);

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("event_id", eventId);
        request.put("event_type", eventType);
        request.put("idempotency_key", idempotencyKey(event, eventId, operationType, resourceType));
        request.put("actor", actor(event.getAuthDetails()));
        request.put("source", "keycloak-event-listener");
        putIfNotBlank(request, "realm", realm(event));
        putIfNotBlank(request, "zone", config.getIrodsAdminZone());
        request.put("keycloak_admin_event", keycloakAdminEvent(event, eventId, operationType, resourceType));
        request.put("irods_admin", irodsAdmin());
        request.put("payload", payload(event));
        return request;
    }

    private Map<String, Object> keycloakAdminEvent(AdminEvent event, String eventId, String operationType, String resourceType) {
        Map<String, Object> body = new LinkedHashMap<>();
        putIfNotBlank(body, "realm", realm(event));
        body.put("event_id", eventId);
        if (event.getTime() > 0) {
            body.put("time", Instant.ofEpochMilli(event.getTime()).toString());
        }
        body.put("operation_type", operationType);
        body.put("resource_type", resourceType);
        putIfNotBlank(body, "resource_path", event.getResourcePath());
        Map<String, Object> representation = representation(event.getRepresentation());
        if (!representation.isEmpty()) {
            body.put("representation", representation);
        }
        if (event.getDetails() != null && !event.getDetails().isEmpty()) {
            body.put("details", new LinkedHashMap<>(event.getDetails()));
        }
        return body;
    }

    private Map<String, Object> irodsAdmin() {
        Map<String, Object> body = new LinkedHashMap<>();
        putIfNotBlank(body, "zone", config.getIrodsAdminZone());
        putIfNotBlank(body, "username", config.getIrodsAdminUsername());
        putIfNotBlank(body, "host", config.getIrodsAdminHost());
        body.put("port", config.getIrodsAdminPort());
        putIfNotBlank(body, "resource", config.getIrodsAdminResource());

        Map<String, Object> credential = new LinkedHashMap<>();
        credential.put("encoding", config.getIrodsAdminCredentialEncoding());
        credential.put("value", config.getIrodsAdminCredentialValue());
        putIfNotBlank(credential, "key_id", config.getIrodsAdminCredentialKeyId());
        body.put("credential", credential);
        return body;
    }

    private Map<String, Object> actor(AuthDetails authDetails) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "keycloak_admin");
        if (authDetails != null) {
            putIfNotBlank(body, "id", authDetails.getUserId());
            putIfNotBlank(body, "client_id", authDetails.getClientId());
        }
        return body;
    }

    private Map<String, Object> payload(AdminEvent event) {
        Map<String, Object> body = new LinkedHashMap<>();
        putIfNotBlank(body, "resource_id", event.getResourceId());
        putIfNotBlank(body, "error", event.getError());
        return body;
    }

    private Map<String, Object> representation(String representation) {
        if (representation == null || representation.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(representation, MAP_TYPE);
        } catch (Exception e) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("raw", representation);
            return body;
        }
    }

    private String idempotencyKey(AdminEvent event, String eventId, String operationType, String resourceType) {
        return String.join(":",
                safe(realm(event)),
                operationType,
                resourceType,
                safe(event.getResourcePath()),
                eventId);
    }

    private String derivedEventId(AdminEvent event) {
        String source = String.join(":",
                safe(realm(event)),
                String.valueOf(event.getTime()),
                value(event.getOperationType()),
                resourceType(event),
                safe(event.getResourcePath()),
                safe(event.getRepresentation()));
        return UUID.nameUUIDFromBytes(source.getBytes(java.nio.charset.StandardCharsets.UTF_8)).toString();
    }

    private String realm(AdminEvent event) {
        if (event.getRealmName() != null && !event.getRealmName().isBlank()) {
            return event.getRealmName();
        }
        return event.getRealmId();
    }

    private String resourceType(AdminEvent event) {
        String resourceType = event.getResourceTypeAsString();
        if (resourceType != null && !resourceType.isBlank()) {
            return resourceType;
        }
        ResourceType typedResource = event.getResourceType();
        return typedResource == null ? "CUSTOM" : typedResource.name();
    }

    private String value(OperationType operationType) {
        return operationType == null ? "ACTION" : operationType.name();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void putIfNotBlank(Map<String, Object> body, String key, String value) {
        if (value != null && !value.isBlank()) {
            body.put(key, value);
        }
    }
}
