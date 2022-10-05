package com.aeontronix.enhancedmule.configmngmt;

import org.junit.Test;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.runtime.api.metadata.TypedValue;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ECfgTestUrl extends MuleArtifactFunctionalTestCase {
    public static final String HTTP = "HTTP";
    public static final String HTTPS = "HTTPS";

    @Override
    protected String getConfigFile() {
        System.setProperty("testUrl3", "https://blue/gray");
        return "testUrl.xml";
    }

    @Test
    public void test() throws Exception {
        final Map<String, TypedValue<?>> vars = flowRunner("test").run().getVariables();
        assetUrl(vars, "t1url", "t1scheme", "t1host", "t1port", "t1path",
                "http://www.be.com", HTTP, "www.be.com", "80", "");
        assetUrl(vars, "t2url", "t2scheme", "t2host", "t2port", "t2path",
                "https://www.be.com:8080/foo", HTTPS, "www.be.com", "8080", "/foo");
        assetUrl(vars, "t3url", "t3scheme", "t3host", "t3port", "t3path",
                "https://blue/gray", HTTPS, "blue", "443", "/gray");
    }

    private void assetUrl(Map<String, TypedValue<?>> vars, String url, String scheme, String host, String port,
                          String path, String expectedUrl, String expectedScheme, String expectedHost, String expectedPort, String expectedPath) {
        assertThat(vars.get(url).getValue(), is(expectedUrl));
        assertThat(vars.get(scheme).getValue(), is(expectedScheme));
        assertThat(vars.get(host).getValue(), is(expectedHost));
        assertThat(vars.get(port).getValue(), is(expectedPort));
        assertThat(vars.get(path).getValue(), is(expectedPath));
    }
}
