package org.irods.keycloak.plugins;

/**
 * Configuration for the thin Keycloak admin-event webhook bridge.
 */
public class IrodsEventListenerConfiguration {
    private String adminServiceEventUrl = "";
    private String sharedSecret = "";
    private String managedRealm = "";
    private String managedGroupRoot = "";
    private int connectTimeoutMillis = 2000;
    private int requestTimeoutMillis = 5000;
    private int maxRetries = 2;
    private String irodsAdminZone = "";
    private String irodsAdminUsername = "";
    private String irodsAdminHost = "";
    private int irodsAdminPort = 1247;
    private String irodsAdminResource = "";
    private String irodsAdminCredentialEncoding = "base64";
    private String irodsAdminCredentialValue = "";
    private String irodsAdminCredentialKeyId = "";

    public boolean isEnabled() {
        return adminServiceEventUrl != null && !adminServiceEventUrl.isBlank();
    }

    public String getAdminServiceEventUrl() {
        return adminServiceEventUrl;
    }

    public void setAdminServiceEventUrl(String adminServiceEventUrl) {
        this.adminServiceEventUrl = adminServiceEventUrl;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public String getManagedRealm() {
        return managedRealm;
    }

    public void setManagedRealm(String managedRealm) {
        this.managedRealm = managedRealm;
    }

    public String getManagedGroupRoot() {
        return managedGroupRoot;
    }

    public void setManagedGroupRoot(String managedGroupRoot) {
        this.managedGroupRoot = managedGroupRoot;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public int getRequestTimeoutMillis() {
        return requestTimeoutMillis;
    }

    public void setRequestTimeoutMillis(int requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public String getIrodsAdminZone() {
        return irodsAdminZone;
    }

    public void setIrodsAdminZone(String irodsAdminZone) {
        this.irodsAdminZone = irodsAdminZone;
    }

    public String getIrodsAdminUsername() {
        return irodsAdminUsername;
    }

    public void setIrodsAdminUsername(String irodsAdminUsername) {
        this.irodsAdminUsername = irodsAdminUsername;
    }

    public String getIrodsAdminHost() {
        return irodsAdminHost;
    }

    public void setIrodsAdminHost(String irodsAdminHost) {
        this.irodsAdminHost = irodsAdminHost;
    }

    public int getIrodsAdminPort() {
        return irodsAdminPort;
    }

    public void setIrodsAdminPort(int irodsAdminPort) {
        this.irodsAdminPort = irodsAdminPort;
    }

    public String getIrodsAdminResource() {
        return irodsAdminResource;
    }

    public void setIrodsAdminResource(String irodsAdminResource) {
        this.irodsAdminResource = irodsAdminResource;
    }

    public String getIrodsAdminCredentialEncoding() {
        return irodsAdminCredentialEncoding;
    }

    public void setIrodsAdminCredentialEncoding(String irodsAdminCredentialEncoding) {
        this.irodsAdminCredentialEncoding = irodsAdminCredentialEncoding;
    }

    public String getIrodsAdminCredentialValue() {
        return irodsAdminCredentialValue;
    }

    public void setIrodsAdminCredentialValue(String irodsAdminCredentialValue) {
        this.irodsAdminCredentialValue = irodsAdminCredentialValue;
    }

    public String getIrodsAdminCredentialKeyId() {
        return irodsAdminCredentialKeyId;
    }

    public void setIrodsAdminCredentialKeyId(String irodsAdminCredentialKeyId) {
        this.irodsAdminCredentialKeyId = irodsAdminCredentialKeyId;
    }
}
