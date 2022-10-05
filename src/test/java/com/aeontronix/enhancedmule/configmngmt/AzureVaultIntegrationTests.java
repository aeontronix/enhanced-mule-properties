package com.aeontronix.enhancedmule.configmngmt;

import org.junit.Assume;
import org.junit.Test;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.runtime.api.metadata.TypedValue;
import org.slf4j.Logger;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.slf4j.LoggerFactory.getLogger;

public class AzureVaultIntegrationTests extends MuleArtifactFunctionalTestCase {
    private static final Logger logger = getLogger(AzureVaultIntegrationTests.class);

    @Override
    protected String getConfigFile() {
        logger.info("Checking if azure tests are enabled");
        try {
            final String azEnabled = System.getProperty("em.azure.vault.enabled");
            Assume.assumeTrue(azEnabled != null && azEnabled.equals("true"));
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw e;
        }
        return "test-azure-vault.xml";
    }

    @Test
    public void testRetrieveSecret() throws Exception {
        final Map<String, TypedValue<?>> vars = flowRunner("test").run().getVariables();
        assertThat(vars.get("res").getValue(), is("verysecret"));
        assertThat(vars.get("res2").getValue(), is("foobarsecret"));
    }
}
