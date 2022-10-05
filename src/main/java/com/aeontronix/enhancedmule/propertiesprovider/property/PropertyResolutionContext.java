package com.aeontronix.enhancedmule.propertiesprovider.property;

import com.aeontronix.enhancedmule.propertiesprovider.property.value.PropertyValue;

public interface PropertyResolutionContext {
    boolean isLocal();

    String getEnvType();

    String getEnvName();

    String getOverrideValue(String key);

    PropertyValue resolve(String value, boolean secure);
}
