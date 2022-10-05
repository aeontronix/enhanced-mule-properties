package com.aeontronix.enhancedmule.propertiesprovider.property;

import com.aeontronix.enhancedmule.propertiesprovider.PropertyResolutionContextImpl;
import com.aeontronix.enhancedmule.propertiesprovider.ResolvedProperties;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.DefaultPropertyValue;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.SelfSignedCertificatePropertyValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class HttpsConfigProperty extends SelfSignedCertConfigProperty {
    @JsonProperty(defaultValue = "false")
    private boolean internal;

    @Override
    protected @NotNull ResolvedProperties buildResolvedProperties(String key, SelfSignedCertificatePropertyValue cert, PropertyResolutionContextImpl context) {
        final ResolvedProperties p = super.buildResolvedProperties(key + ".ks", cert, context);
        final String portKey = key + ".port";
        final String hostKey = key + ".host";
        String port = context.getOverrideValue(portKey);
        String host = context.getOverrideValue(hostKey);
        if (host == null) {
            host = "0.0.0.0";
        }
        if (port == null) {
            port = internal ? "8092" : "8082";
        }
        p.add(portKey, new DefaultPropertyValue(port, false));
        p.add(portKey, new DefaultPropertyValue(port, false));
        p.add(hostKey, new DefaultPropertyValue(host, false));
        return p;
    }
}
