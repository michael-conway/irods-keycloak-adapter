package org.irods.keycloak.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.AuthDetails;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

import java.util.Map;

public class IrodsKeycloakEventRequestBuilderTest extends AbstractIrodsTest {

    @Test
    public void testBuildsGoAdminEventContractPayload() {
        IrodsEventListenerConfiguration config = new IrodsEventListenerConfiguration();
        config.setIrodsAdminZone("tempZone");
        config.setIrodsAdminUsername("rods");
        config.setIrodsAdminHost("127.0.0.1");
        config.setIrodsAdminResource("providerResc");
        config.setIrodsAdminCredentialValue("encoded");
        config.setIrodsAdminCredentialKeyId("local");

        AuthDetails authDetails = new AuthDetails();
        authDetails.setUserId("admin-user-id");
        authDetails.setClientId("security-admin-console");

        AdminEvent event = new AdminEvent();
        event.setId("event-1");
        event.setTime(1782475200000L);
        event.setRealmName("irods");
        event.setAuthDetails(authDetails);
        event.setOperationType(OperationType.CREATE);
        event.setResourceType(ResourceType.USER);
        event.setResourcePath("users/user-id");
        event.setRepresentation("{\"username\":\"alice\"}");

        Map<String, Object> request = new IrodsKeycloakEventRequestBuilder(config, new ObjectMapper()).build(event);

        Assert.assertEquals("event-1", request.get("event_id"));
        Assert.assertEquals("keycloak.admin.user.create", request.get("event_type"));
        Assert.assertEquals("irods:CREATE:USER:users/user-id:event-1", request.get("idempotency_key"));

        Map<String, Object> keycloakAdminEvent = castMap(request.get("keycloak_admin_event"));
        Assert.assertEquals("irods", keycloakAdminEvent.get("realm"));
        Assert.assertEquals("event-1", keycloakAdminEvent.get("event_id"));
        Assert.assertEquals("CREATE", keycloakAdminEvent.get("operation_type"));
        Assert.assertEquals("USER", keycloakAdminEvent.get("resource_type"));
        Assert.assertEquals("users/user-id", keycloakAdminEvent.get("resource_path"));
        Assert.assertEquals("alice", castMap(keycloakAdminEvent.get("representation")).get("username"));

        Map<String, Object> irodsAdmin = castMap(request.get("irods_admin"));
        Assert.assertEquals("tempZone", irodsAdmin.get("zone"));
        Assert.assertEquals("rods", irodsAdmin.get("username"));
        Assert.assertEquals("127.0.0.1", irodsAdmin.get("host"));
        Assert.assertEquals(1247, irodsAdmin.get("port"));
        Assert.assertEquals("providerResc", irodsAdmin.get("resource"));
        Assert.assertEquals("encoded", castMap(irodsAdmin.get("credential")).get("value"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object value) {
        return (Map<String, Object>) value;
    }
}
