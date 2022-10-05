package com.aeontronix.enhancedmule.propertiesprovider.property;

import com.aeontronix.enhancedmule.propertiesprovider.PropertyResolutionContextImpl;
import com.aeontronix.enhancedmule.propertiesprovider.ResolvedProperties;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.PropertyValue;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@JsonIgnoreProperties("type")
public class TextConfigProperty implements ConfigProperty {
    private static final Logger logger = getLogger(TextConfigProperty.class);
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private boolean secure;
    @JsonProperty
    private boolean required;
    @JsonProperty("default")
    private String defaultValue;

    public TextConfigProperty() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public PropertyValue resolveProperty(String key, PropertyResolutionContextImpl context) throws PropertyResolutionException {
        return resolveProperty(key, context, false);
    }

    public PropertyValue resolveProperty(String key, PropertyResolutionContextImpl context, boolean forceSecure) throws PropertyResolutionException {
        final boolean propSecure = forceSecure || secure;
        final String override = context.getOverrideValue(key);
        if (override != null) {
            logger.debug("Override system property found for property {}: {}", key, override);
            return context.resolve(override,propSecure);
        }
        String value = null;
        if ( defaultValue != null) {
            value = defaultValue;
            logger.debug("Assigned property {} with default value {}", key, valueToString(value));
        } else if (logger.isDebugEnabled()) {
            logger.debug("No default value for {}", key);
        }
        if (value != null) {
            return context.resolve(value,propSecure);
        } else if( required && context.isPropertiesValidation() ) {
            throw new PropertyResolutionException("No value for property found: "+key);
        } else {
            return null;
        }
    }

    private String valueToString(String value) {
        if (value == null) {
            return "";
        } else if (isSecure()) {
            return "********";
        } else {
            return value;
        }
    }

    private Map<String, String> toLowerCaseMap(HashMap<String, String> map) {
        final HashMap<String, String> newMap = new HashMap<>();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                newMap.put(entry.getKey().toLowerCase(), entry.getValue());
            }
        }
        return newMap;
    }

    @Override
    public ResolvedProperties resolveProperties(String key, PropertyResolutionContextImpl context) throws PropertyResolutionException {
        final PropertyValue property = resolveProperty(key, context);
        if (property != null) {
            return new ResolvedProperties(key, property);
        } else {
            return new ResolvedProperties();
        }
    }

    @Override
    public String toString() {
        return "DefaultConfigProperty{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", secure=" + secure +
                ", required=" + required +
                ", defaultValue=" + defaultValue +
                '}';
    }
}
