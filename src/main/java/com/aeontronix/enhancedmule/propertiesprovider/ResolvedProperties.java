package com.aeontronix.enhancedmule.propertiesprovider;

import com.aeontronix.enhancedmule.propertiesprovider.property.value.PropertyValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ResolvedProperties {
    private @NotNull
    final Map<String, PropertyValue> properties = new HashMap<>();

    public ResolvedProperties() {
    }

    public ResolvedProperties(@Nullable Map<String, PropertyValue> properties) {
        add(properties);
    }

    public ResolvedProperties(String propKey, PropertyValue propValue) {
        properties.put(propKey, propValue);
    }

    public void add(ResolvedProperties properties) {
        add(properties.getProperties());
    }

    public void add(String key, PropertyValue value) {
        properties.put(key, value);
    }

    private void add(Map<String, PropertyValue> properties) {
        if (properties != null) {
            this.properties.putAll(properties);
        }
    }

    public @NotNull Map<String, PropertyValue> getProperties() {
        return properties;
    }

    public PropertyValue get(String key) {
        return properties.get(key);
    }
}
