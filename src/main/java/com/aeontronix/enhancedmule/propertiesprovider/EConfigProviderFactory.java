package com.aeontronix.enhancedmule.propertiesprovider;

import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.config.api.dsl.model.ConfigurationParameters;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory;
import org.slf4j.Logger;

import static com.aeontronix.enhancedmule.propertiesprovider.EConfigExtensionLoadingDelegate.CONFIG_ELEMENT;
import static org.mule.runtime.api.component.ComponentIdentifier.builder;
import static org.slf4j.LoggerFactory.getLogger;

public class EConfigProviderFactory implements ConfigurationPropertiesProviderFactory {
    public static final String EXTENSION_NAMESPACE = "enhanced-mule-properties";
    private static final Logger logger = getLogger(EConfigProviderFactory.class);
    private static final ComponentIdentifier CUSTOM_PROPERTIES_PROVIDER =
            builder().namespace(EXTENSION_NAMESPACE).name(CONFIG_ELEMENT).build();

    @Override
    public ComponentIdentifier getSupportedComponentIdentifier() {
        return CUSTOM_PROPERTIES_PROVIDER;
    }

    @Override
    public ConfigurationPropertiesProvider createProvider(ConfigurationParameters parameters,
                                                          ResourceProvider externalResourceProvider) {
        return new EConfigProvider(externalResourceProvider, parameters);
    }
}
