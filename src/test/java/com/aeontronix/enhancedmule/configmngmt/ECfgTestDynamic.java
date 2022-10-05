package com.aeontronix.enhancedmule.configmngmt;

import org.junit.Test;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.runtime.api.metadata.TypedValue;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class ECfgTestDynamic extends MuleArtifactFunctionalTestCase {
    @Override
    protected String getConfigFile() {
        System.setProperty("anypoint.env.name","dev");
        System.setProperty("anypoint.env.type","SANDBOXX");
        System.setProperty("lol","tre");
        return "properties-dynamic.xml";
    }

    @Test
    public void testStringDefaultValue() throws Exception {
        final Map<String, TypedValue<?>> vars = flowRunner("test").run().getVariables();
        final Object test1 = vars.get("test1").getValue();
        final Object test2 = vars.get("test2").getValue();
        assertThat("test values are different",!test1.equals(test2));
    }
}
