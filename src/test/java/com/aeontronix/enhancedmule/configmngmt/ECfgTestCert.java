package com.aeontronix.enhancedmule.configmngmt;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.runtime.api.metadata.TypedValue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Map;

public class ECfgTestCert extends MuleArtifactFunctionalTestCase {
    @Override
    protected String getConfigFile() {
        System.setProperty("anypoint.env.name","local");
        System.setProperty("anypoint.env.type","local");
        return "testCert.xml";
    }

    @Test
    public void test() throws Exception {
        final Map<String, TypedValue<?>> vars = flowRunner("test").run().getVariables();
        final String cert = (String)vars.get("cert").getValue();
        System.out.println(cert);
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
