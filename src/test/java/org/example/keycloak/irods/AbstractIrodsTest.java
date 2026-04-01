package org.example.keycloak.irods;

import org.junit.Before;
import java.util.Properties;

public abstract class AbstractIrodsTest {
    protected Properties testProperties;
    protected TestPropertiesHelper propertiesHelper;

    @Before
    public void init() {
        propertiesHelper = new TestPropertiesHelper();
        testProperties = propertiesHelper.getProperties();
    }
}
