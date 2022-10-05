package com.aeontronix.enhancedmule.configmngmt;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.runtime.api.metadata.TypedValue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ECfgTestHttps extends MuleArtifactFunctionalTestCase {
    @Override
    protected String getConfigFile() {
        System.setProperty("anypoint.env.name","local");
        System.setProperty("anypoint.env.type","local");
        return "testHttps.xml";
    }

    @Test
    public void test() throws Exception {
        final Map<String, TypedValue<?>> vars = flowRunner("test").run().getVariables();
        assertThat(vars.get("host").getValue(), is("0.0.0.0"));
        assertThat(vars.get("port").getValue(), is("8092"));
        final String cert = (String)vars.get("cert").getValue();
        final String storepw = (String) vars.get("storepw").getValue();
        final String keypw = (String) vars.get("keypw").getValue();
        final String alias = (String) vars.get("alias").getValue();
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try(final InputStream is = new ByteArrayInputStream(new Base64().decode(cert))) {
            keyStore.load(is, storepw.toCharArray());
        }
        keyStore.getEntry(alias, new KeyStore.PasswordProtection(keypw.toCharArray()));
    }
}
