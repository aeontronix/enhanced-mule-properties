package com.aeontronix.enhancedmule.propertiesprovider.property;

import com.aeontronix.enhancedmule.propertiesprovider.PropertyResolutionContextImpl;
import com.aeontronix.enhancedmule.propertiesprovider.ResolvedProperties;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.GeneratedPropertyValue;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.PropertyValue;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.SelfSignedCertificatePropertyValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class SelfSignedCertConfigProperty extends TextConfigProperty {
    @JsonProperty
    public static final String algorithm = "RSA";
    @JsonProperty
    public static final String signAlgorithm = "SHA512WithRSAEncryption";
    @JsonProperty
    private int keySize = 2048;

    @Override
    public ResolvedProperties resolveProperties(String key, PropertyResolutionContextImpl context) throws PropertyResolutionException {
        PropertyValue dn = resolveProperty(key, context, true);
        final SelfSignedCertificatePropertyValue cert = new SelfSignedCertificatePropertyValue(dn, algorithm, signAlgorithm, keySize);
        return buildResolvedProperties(key, cert, context);
    }

    @NotNull
    protected ResolvedProperties buildResolvedProperties(String key, SelfSignedCertificatePropertyValue cert, PropertyResolutionContextImpl context) {
        final ResolvedProperties resolvedProperties = new ResolvedProperties();
        resolvedProperties.add(key + ".file", cert);
        resolvedProperties.add(key + ".alias", new GeneratedPropertyValue(cert, false, s -> cert.getAlias()));
        resolvedProperties.add(key + ".storepw", new GeneratedPropertyValue(cert, true, s -> new String(cert.getStorePassword())));
        resolvedProperties.add(key + ".keypw", new GeneratedPropertyValue(cert, true, s -> new String(cert.getKeyPassword())));
        return resolvedProperties;
    }
}
