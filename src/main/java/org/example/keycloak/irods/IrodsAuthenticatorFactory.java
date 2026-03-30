package org.example.keycloak.irods;

import java.util.List;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

public class IrodsAuthenticatorFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "irods-authenticator";
    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENTS = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.ALTERNATIVE,
            AuthenticationExecutionModel.Requirement.DISABLED
    };

    @Override
    public Authenticator create(KeycloakSession session) {
        return new IrodsAuthenticator(new StubIrodsAuthService());
    }

    @Override
    public void init(Config.Scope config) {
        // static provider initialization hook
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // no-op in standalone skeleton
    }

    @Override
    public void close() {
        // no-op in standalone skeleton
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "iRODS Authenticator";
    }

    @Override
    public String getReferenceCategory() {
        return "irods";
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENTS;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "Standalone skeleton for an iRODS-oriented authenticator. Wire a real iRODS adapter in a second step.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        ProviderConfigProperty mode = new ProviderConfigProperty();
        mode.setName("adapterMode");
        mode.setLabel("Adapter Mode");
        mode.setType(ProviderConfigProperty.STRING_TYPE);
        mode.setHelpText("Reserved for a later integration step, for example http, pam, or token-exchange.");

        ProviderConfigProperty principalMapper = new ProviderConfigProperty();
        principalMapper.setName("principalMapper");
        principalMapper.setLabel("Principal Mapper");
        principalMapper.setType(ProviderConfigProperty.STRING_TYPE);
        principalMapper.setHelpText("Reserved mapping strategy name for translating external identities to Keycloak users.");

        return List.of(mode, principalMapper);
    }

    @Override
    public String getDisplayCategory() {
        return "iRODS";
    }
}
