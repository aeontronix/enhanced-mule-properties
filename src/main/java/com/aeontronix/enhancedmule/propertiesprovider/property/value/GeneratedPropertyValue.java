package com.aeontronix.enhancedmule.propertiesprovider.property.value;

import com.aeontronix.enhancedmule.propertiesprovider.property.PropertyResolutionException;

import java.io.IOException;

public class GeneratedPropertyValue extends PropertyValue {
    protected PropertyValue source;
    protected GeneratedPropertyValueGenerator generator;
    protected boolean secure;

    public GeneratedPropertyValue(PropertyValue source, boolean secure, GeneratedPropertyValueGenerator generator) {
        this.source = source;
        this.generator = generator;
        this.secure = secure;
    }

    @Override
    public String evaluate() throws PropertyResolutionException {
        try {
            return generator.generate(source);
        } catch (Exception e) {
            throw new PropertyResolutionException(e);
        }
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public String getRawValue() {
        return "GENERATED";
    }

    @Override
    public void close() throws IOException {
    }
}
