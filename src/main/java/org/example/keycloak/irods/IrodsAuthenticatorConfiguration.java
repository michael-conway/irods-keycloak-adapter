package org.example.keycloak.irods;

/**
 * Configuration necessary to do irods authentication
 */
public class IrodsAuthenticatorConfiguration {

    private String irodsHost = "";
    private int irodsPort = 1247;
    private String irodsZone = "";

    public String getIrodsHost() {
        return irodsHost;
    }

    public void setIrodsHost(String irodsHost) {
        this.irodsHost = irodsHost;
    }

    public int getIrodsPort() {
        return irodsPort;
    }

    public void setIrodsPort(int irodsPort) {
        this.irodsPort = irodsPort;
    }

    public String getIrodsZone() {
        return irodsZone;
    }

    public void setIrodsZone(String irodsZone) {
        this.irodsZone = irodsZone;
    }



}
