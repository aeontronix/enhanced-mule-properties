package com.aeontronix.enhancedmule.propertiesprovider.property;

import com.aeontronix.enhancedmule.propertiesprovider.PropertyResolutionContextImpl;
import com.aeontronix.enhancedmule.propertiesprovider.ResolvedProperties;
import org.slf4j.Logger;

import java.util.HashMap;

import static org.slf4j.LoggerFactory.getLogger;

public class ConfigProperties extends HashMap<String,ConfigProperty> {
    private static final Logger logger = getLogger(ConfigProperties.class);

    public ResolvedProperties resolveProperties(PropertyResolutionContextImpl propertyResolutionContext) throws PropertyResolutionException {
        logger.debug("Resolving properties");
        final ResolvedProperties resolvedProperties = new ResolvedProperties();
        for (Entry<String, ConfigProperty> entry : entrySet()) {
            final ConfigProperty value = entry.getValue();
            logger.debug("Resolving property {} = {}",entry.getKey(),value);
            if( value != null ) {
                resolvedProperties.add(value.resolveProperties(entry.getKey(),propertyResolutionContext));
            }
        }
        return resolvedProperties;
    }
}
