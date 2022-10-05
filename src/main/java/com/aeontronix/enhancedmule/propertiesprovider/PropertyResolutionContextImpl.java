package com.aeontronix.enhancedmule.propertiesprovider;

import com.aeontronix.enhancedmule.propertiesprovider.azurevault.AzureVaultProvider;
import com.aeontronix.enhancedmule.propertiesprovider.property.PropertyResolutionContext;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.PropertyValue;
import com.aeontronix.kryptotek.CryptoUtils;
import com.aeontronix.kryptotek.key.DecryptionKey;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PropertyResolutionContextImpl implements PropertyResolutionContext {
    private final ExpressionProcessor expressionProcessor;
    private final boolean local;
    private final String envType;
    private final String envName;
    private DecryptionKey decryptionKey;
    private final Map<String, String> overrides;
    private final boolean propertiesValidation;

    public PropertyResolutionContextImpl(String envType, String envName, String cryptoKey, Map<String, String> overrides,
                                         AzureVaultProvider azureVaultProvider, boolean propertiesValidation) throws IOException {
        this.envType = envType;
        this.envName = envName;
        local = envType.equals("local");
        this.overrides = overrides;
        this.propertiesValidation = propertiesValidation;
        if (cryptoKey != null) {
            try {
                decryptionKey = (DecryptionKey) CryptoUtils.readKey(cryptoKey.getBytes(UTF_8));
            } catch (ClassCastException e) {
                throw new IOException("Invalid crypto key (key isn't a decryption key)");
            } catch (InvalidKeyException e) {
                throw new IOException("Invalid encryption key");
            }
        }
        expressionProcessor = new ExpressionProcessor(azureVaultProvider, decryptionKey);
    }

    @Override
    public boolean isLocal() {
        return local;
    }

    @Override
    public String getEnvType() {
        return envType;
    }

    @Override
    public String getEnvName() {
        return envName;
    }

    @Override
    public String getOverrideValue(String key) {
        String property = System.getProperty(key);
        if (property == null) {
            property = System.getProperty(key + "*");
        }
        if (property == null && overrides != null) {
            property = overrides.get(key);
        }
        return property;
    }

    public boolean isPropertiesValidation() {
        return propertiesValidation;
    }

    @Override
    public PropertyValue resolve(String value, boolean secure) {
        return expressionProcessor.resolveExpression(value, secure);
    }
}
