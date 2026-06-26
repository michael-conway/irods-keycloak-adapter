package org.irods.keycloak.plugins;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class IrodsKeycloakEventHttpClient {
    private static final Logger LOG = Logger.getLogger(IrodsKeycloakEventHttpClient.class);
    static final String SHARED_SECRET_HEADER = "X-IRODS-KC-Shared-Secret";

    private final IrodsEventListenerConfiguration config;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public IrodsKeycloakEventHttpClient(IrodsEventListenerConfiguration config, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(config.getConnectTimeoutMillis()))
                .build();
    }

    public void post(Map<String, Object> requestBody) {
        String json;
        try {
            json = objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            LOG.errorf(e, "Failed to serialize Keycloak admin event callback");
            return;
        }

        for (int attempt = 0; attempt <= config.getMaxRetries(); attempt++) {
            try {
                HttpResponse<String> response = httpClient.send(buildRequest(json), HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 202) {
                    LOG.debugf("Submitted Keycloak admin event callback to %s", config.getAdminServiceEventUrl());
                    return;
                }
                if (response.statusCode() < 500) {
                    LOG.warnf("Keycloak admin event callback rejected with status %d: %s",
                            response.statusCode(), response.body());
                    return;
                }
                LOG.warnf("Keycloak admin event callback returned retryable status %d on attempt %d",
                        response.statusCode(), attempt + 1);
            } catch (IOException e) {
                LOG.warnf(e, "Keycloak admin event callback failed on attempt %d", attempt + 1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.warnf(e, "Interrupted while submitting Keycloak admin event callback");
                return;
            } catch (IllegalArgumentException e) {
                LOG.errorf(e, "Invalid Keycloak event admin service URL: %s", config.getAdminServiceEventUrl());
                return;
            }
        }

        LOG.errorf("Exhausted retries submitting Keycloak admin event callback to %s", config.getAdminServiceEventUrl());
    }

    private HttpRequest buildRequest(String json) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(config.getAdminServiceEventUrl()))
                .timeout(Duration.ofMillis(config.getRequestTimeoutMillis()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));

        if (config.getSharedSecret() != null && !config.getSharedSecret().isBlank()) {
            builder.header(SHARED_SECRET_HEADER, config.getSharedSecret());
        }

        return builder.build();
    }
}
