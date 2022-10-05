package com.aeontronix.enhancedmule.propertiesprovider.property.value;

import com.aeontronix.enhancedmule.propertiesprovider.property.PropertyResolutionException;

import java.io.Closeable;

public abstract class PropertyValue implements Closeable {
    public abstract boolean isSecure();
    public abstract String evaluate() throws PropertyResolutionException;
    public abstract String getRawValue();
}
