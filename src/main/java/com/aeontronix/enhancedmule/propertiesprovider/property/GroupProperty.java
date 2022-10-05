package com.aeontronix.enhancedmule.propertiesprovider.property;

import com.aeontronix.enhancedmule.propertiesprovider.PropertyResolutionContextImpl;
import com.aeontronix.enhancedmule.propertiesprovider.ResolvedProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties("type")
public class GroupProperty implements ConfigProperty {
    private HashMap<String, ConfigProperty> properties;

    public HashMap<String, ConfigProperty> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, ConfigProperty> properties) {
        this.properties = properties;
    }

    @Override
    public ResolvedProperties resolveProperties(String key, PropertyResolutionContextImpl context) throws PropertyResolutionException {
        final ResolvedProperties grouped = new ResolvedProperties();
        for (Map.Entry<String, ConfigProperty> entry : properties.entrySet()) {
            grouped.add(entry.getValue().resolveProperties(key + "." + entry.getKey(), context));
        }
        return grouped;
    }

    @Override
    public String toString() {
        return "GroupProperty{" +
                "properties=" + properties +
                '}';
    }
}
