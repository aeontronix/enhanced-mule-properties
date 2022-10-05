package com.aeontronix.enhancedmule.propertiesprovider.property;

import com.aeontronix.enhancedmule.propertiesprovider.PropertyResolutionContextImpl;
import com.aeontronix.enhancedmule.propertiesprovider.ResolvedProperties;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.PropertyValue;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.TempFilePropertyValue;

public class FileConfigProperty extends TextConfigProperty {
    @Override
    public ResolvedProperties resolveProperties(String key, PropertyResolutionContextImpl context) throws PropertyResolutionException {
        final PropertyValue contentValue = resolveProperty(key, context);
        final ResolvedProperties resolvedProperties = new ResolvedProperties();
        if (contentValue != null) {
            resolvedProperties.add(key, contentValue);
            resolvedProperties.add(key + ".file", new TempFilePropertyValue(contentValue));
        }
        return resolvedProperties;
    }
}
