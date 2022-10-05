package com.aeontronix.enhancedmule.propertiesprovider.property;

import com.aeontronix.enhancedmule.propertiesprovider.PropertyResolutionContextImpl;
import com.aeontronix.enhancedmule.propertiesprovider.ResolvedProperties;

public interface ConfigProperty {
    ResolvedProperties resolveProperties(String key, PropertyResolutionContextImpl context) throws PropertyResolutionException;
}
