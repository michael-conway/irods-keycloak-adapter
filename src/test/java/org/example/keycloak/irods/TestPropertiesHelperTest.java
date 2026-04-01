package org.example.keycloak.irods;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestPropertiesHelperTest extends AbstractIrodsTest {

    @Test
    public void testAbstractIrodsTestInitializesProperties() {
        assertNotNull(testProperties);
        assertEquals("test1", testProperties.getProperty("test_user_1"));
    }

    @Test
    public void testLoadProperties() {
        TestPropertiesHelper helper = new TestPropertiesHelper();
        assertNotNull(helper.getProperties());
        
        // Check for properties from testing.properties
        // 1:test_user_1="test1"
        // 2:test_user_1_password="test"
        
        assertEquals("test1", helper.getProperty("test_user_1"));
        assertEquals("test", helper.getProperty("test_user_1_password"));
    }
}
