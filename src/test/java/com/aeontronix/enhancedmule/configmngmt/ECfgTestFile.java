package com.aeontronix.enhancedmule.configmngmt;

import org.junit.Test;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.runtime.api.metadata.TypedValue;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ECfgTestFile extends MuleArtifactFunctionalTestCase {
    @Override
    protected String getConfigFile() {
        System.setProperty("anypoint.env.name","local");
        System.setProperty("anypoint.env.type","local");
        return "testFile.xml";
    }

    @Test
    public void test() throws Exception {
        final Map<String, TypedValue<?>> vars = flowRunner("test").run().getVariables();
        assertThat(vars.get("testFile").getValue(), is("foo"));
    }
}
