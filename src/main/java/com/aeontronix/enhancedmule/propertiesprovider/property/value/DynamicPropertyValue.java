package com.aeontronix.enhancedmule.propertiesprovider.property.value;

import com.aeontronix.enhancedmule.propertiesprovider.property.PropertyResolutionException;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class DynamicPropertyValue extends DefaultPropertyValue {
    private Duration cacheTtl;
    private LocalDateTime cacheExpiry;
    private String cachedValue;

    public DynamicPropertyValue(String value, boolean secure, Duration cacheTtl) {
        super(value, secure);
        this.cacheTtl = cacheTtl;
    }

    @Override
    public final synchronized String evaluate() throws PropertyResolutionException {
        if (cachedValue != null) {
            if (LocalDateTime.now().isAfter(cacheExpiry)) {
                cachedValue = null;
                cacheExpiry = null;
            } else {
                return cachedValue;
            }
        }
        String value = evaluateDynamically();
        if (cacheTtl != null) {
            cachedValue = value;
            cacheExpiry = LocalDateTime.now().plus(cacheTtl);
            return cachedValue;
        } else {
            return value;
        }
    }

    protected abstract String evaluateDynamically();
}
