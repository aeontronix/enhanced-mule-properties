package com.aeontronix.enhancedmule.propertiesprovider.property;

import com.aeontronix.enhancedmule.propertiesprovider.PropertyResolutionContextImpl;
import com.aeontronix.enhancedmule.propertiesprovider.ResolvedProperties;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.GeneratedPropertyValue;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.PropertyValue;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

public class URLConfigProperty extends TextConfigProperty {
    @Override
    public ResolvedProperties resolveProperties(String key, PropertyResolutionContextImpl context) throws PropertyResolutionException {
        final PropertyValue url = resolveProperty(key, context);
        if (url != null) {
            HashMap<String, PropertyValue> properties = new HashMap<>();
            properties.put(key, url);
            properties.put(key + ".scheme", new GeneratedPropertyValue(url, false, source -> new URL(source.evaluate()).getProtocol().toUpperCase(Locale.ROOT)));
            properties.put(key + ".host", new GeneratedPropertyValue(url, false, source -> new URL(source.evaluate()).getHost()));
            properties.put(key + ".port", new GeneratedPropertyValue(url, false, source -> new URL(source.evaluate()).getHost()));
            properties.put(key + ".path", new GeneratedPropertyValue(url, false, source -> {
                {
                    String path = new URL(source.evaluate()).getPath();
                    if (path == null) {
                        path = "";
                    }
                    return path;
                }
            }));
            properties.put(key + ".port", new GeneratedPropertyValue(url, false, source -> {
                {
                    final int port = new URL(source.evaluate()).getPort();
                    if (port == -1) {
                        final String scheme = new URL(source.evaluate()).getProtocol();
                        if (scheme.equalsIgnoreCase("HTTPS")) {
                            return "443";
                        } else {
                            return "80";
                        }
                    } else {
                        return Integer.toString(port);
                    }
                }
            }));
            return new ResolvedProperties(properties);
        } else {
            return new ResolvedProperties();
        }
    }
}
