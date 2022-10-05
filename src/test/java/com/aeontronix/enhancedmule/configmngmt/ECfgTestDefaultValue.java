package com.aeontronix.enhancedmule.configmngmt;

import org.junit.Test;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.runtime.api.metadata.TypedValue;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ECfgTestDefaultValue extends MuleArtifactFunctionalTestCase {
    @Override
    protected String getConfigFile() {
        System.setProperty("anypoint.env.name","dev");
        System.setProperty("anypoint.env.type","SANDBOXX");
        System.setProperty("lol","tre");
        return "properties-defaultvalue.xml";
    }

    @Test
    public void testStringDefaultValue() throws Exception {
        final Map<String, TypedValue<?>> vars = flowRunner("test").run().getVariables();
        assertThat(vars.get("test").getValue(), is("sometest"));
    }

    @Test
    public void testNumberDefaultValue() throws Exception {
        final Map<String, TypedValue<?>> vars = flowRunner("testnb").run().getVariables();
        assertThat(vars.get("testnb").getValue(), is("44"));
    }
}
