package org.irods.keycloak.plugins;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.ResourceType;

import java.util.Map;
import java.util.Set;

public class IrodsKeycloakEventListenerProvider implements EventListenerProvider {
    private static final Logger LOG = Logger.getLogger(IrodsKeycloakEventListenerProvider.class);
    private static final Set<ResourceType> SUPPORTED_RESOURCE_TYPES = Set.of(
            ResourceType.USER,
            ResourceType.GROUP,
            ResourceType.GROUP_MEMBERSHIP
    );

    private final IrodsEventListenerConfiguration config;
    private final IrodsKeycloakEventRequestBuilder requestBuilder;
    private final IrodsKeycloakEventHttpClient httpClient;

    public IrodsKeycloakEventListenerProvider(IrodsEventListenerConfiguration config,
                                              IrodsKeycloakEventRequestBuilder requestBuilder,
                                              IrodsKeycloakEventHttpClient httpClient) {
        this.config = config;
        this.requestBuilder = requestBuilder;
        this.httpClient = httpClient;
    }

    @Override
    public void onEvent(Event event) {
        // Login events are intentionally ignored. This bridge only mirrors admin mutations.
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        if (!config.isEnabled()) {
            return;
        }
        if (!isSupported(event)) {
            return;
        }

        Map<String, Object> request = requestBuilder.build(event);
        httpClient.post(request);
    }

    @Override
    public void close() {
        // no-op
    }

    private boolean isSupported(AdminEvent event) {
        if (event == null) {
            return false;
        }
        if (event.getError() != null && !event.getError().isBlank()) {
            LOG.debugf("Skipping failed Keycloak admin event %s: %s", event.getId(), event.getError());
            return false;
        }
        if (config.getManagedRealm() != null && !config.getManagedRealm().isBlank()) {
            String realm = event.getRealmName() == null || event.getRealmName().isBlank()
                    ? event.getRealmId()
                    : event.getRealmName();
            if (!config.getManagedRealm().equals(realm)) {
                return false;
            }
        }
        ResourceType resourceType = resourceType(event);
        if (!SUPPORTED_RESOURCE_TYPES.contains(resourceType)) {
            return false;
        }
        return isInManagedGroupRoot(event);
    }

    private boolean isInManagedGroupRoot(AdminEvent event) {
        String managedGroupRoot = config.getManagedGroupRoot();
        if (managedGroupRoot == null || managedGroupRoot.isBlank() || resourceType(event) == ResourceType.USER) {
            return true;
        }

        if (containsManagedGroupRoot(event.getResourcePath(), managedGroupRoot)
                || containsManagedGroupRoot(event.getRepresentation(), managedGroupRoot)) {
            return true;
        }

        if (event.getDetails() != null) {
            return event.getDetails().values().stream()
                    .anyMatch(value -> containsManagedGroupRoot(value, managedGroupRoot));
        }

        return false;
    }

    private boolean containsManagedGroupRoot(String value, String managedGroupRoot) {
        return value != null && (value.equals(managedGroupRoot)
                || value.startsWith(managedGroupRoot + "/")
                || value.contains(managedGroupRoot + "/")
                || value.contains("\"" + managedGroupRoot + "\""));
    }

    private ResourceType resourceType(AdminEvent event) {
        if (event.getResourceType() != null) {
            return event.getResourceType();
        }
        String resourceType = event.getResourceTypeAsString();
        if (resourceType == null || resourceType.isBlank()) {
            return null;
        }
        try {
            return ResourceType.valueOf(resourceType);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
