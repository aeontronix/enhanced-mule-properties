package com.aeontronix.enhancedmule.propertiesprovider.property.value;

import com.aeontronix.enhancedmule.propertiesprovider.property.PropertyResolutionException;

import java.io.IOException;

public class DefaultPropertyValue extends PropertyValue {
    protected String value;
    protected final boolean secure;

    public DefaultPropertyValue(String value, boolean secure) {
        this.value = value;
        this.secure = secure;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public synchronized void close() throws IOException {
    }

    @Override
    public synchronized String evaluate() throws PropertyResolutionException {
        return value;
    }

    @Override
    public synchronized String getRawValue() {
        return value;
    }
}
