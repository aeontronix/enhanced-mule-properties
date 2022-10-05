package com.aeontronix.enhancedmule.propertiesprovider.property.value;

import java.util.UUID;

public class UUIDPropertyValue extends DynamicPropertyValue {
    public UUIDPropertyValue(boolean secure, String rawValue) {
        super(rawValue, secure, null);
    }

    @Override
    protected synchronized String evaluateDynamically() {
        return UUID.randomUUID().toString();
    }
}
