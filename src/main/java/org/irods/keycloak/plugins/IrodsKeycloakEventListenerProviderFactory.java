package org.irods.keycloak.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class IrodsKeycloakEventListenerProviderFactory implements EventListenerProviderFactory {
    public static final String PROVIDER_ID = "irods-keycloak-admin-events";
    private static final Logger LOG = Logger.getLogger(IrodsKeycloakEventListenerProviderFactory.class);

    private IrodsEventListenerConfiguration config;
    private ObjectMapper objectMapper;

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        IrodsKeycloakEventRequestBuilder requestBuilder = new IrodsKeycloakEventRequestBuilder(config, objectMapper);
        IrodsKeycloakEventHttpClient httpClient = new IrodsKeycloakEventHttpClient(config, objectMapper);
        return new IrodsKeycloakEventListenerProvider(config, requestBuilder, httpClient);
    }

    @Override
    public void init(Config.Scope scope) {
        this.config = new IrodsEventListenerConfigurationService().initConfiguration();
        this.objectMapper = new ObjectMapper();
        if (config.isEnabled()) {
            LOG.infof("Initialized iRODS Keycloak admin-event listener for %s", config.getAdminServiceEventUrl());
        } else {
            LOG.info("Initialized iRODS Keycloak admin-event listener in disabled mode");
        }
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // no-op
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
