package com.aeontronix.enhancedmule.propertiesprovider;

import com.aeontronix.enhancedmule.propertiesprovider.azurevault.AzureVaultProvider;
import com.aeontronix.enhancedmule.propertiesprovider.property.PropertyResolutionException;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.AzureVaultPropertyValue;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.DefaultPropertyValue;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.PropertyValue;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.UUIDPropertyValue;
import com.aeontronix.kryptotek.CryptoUtils;
import com.aeontronix.kryptotek.DecryptionException;
import com.aeontronix.kryptotek.key.DecryptionKey;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionProcessor {
    private final AzureVaultProvider azureVaultProvider;
    private final DecryptionKey decryptionKey;
    private Pattern expressionRegex = Pattern.compile("^\\{\\{(\\w*)?:(.*)\\}\\}$");

    public ExpressionProcessor(AzureVaultProvider azureVaultProvider, DecryptionKey decryptionKey) {
        this.azureVaultProvider = azureVaultProvider;
        this.decryptionKey = decryptionKey;
    }

    public PropertyValue resolveExpression(String value, boolean secure) {
        final Matcher expMatcher = expressionRegex.matcher(value);
        if (expMatcher.find()) {
            String expType = expMatcher.group(1).toLowerCase();
            String expValue = expMatcher.group(2);
            if (expType.equals("raw")) {
                return new DefaultPropertyValue(expValue, secure);
            } else if (expType.equals("encrypted")) {
                return processEncryptedException(secure, expValue);
            } else if (expType.equals("azvault")) {
                if (azureVaultProvider == null) {
                    throw new PropertyResolutionException("Unable to process azvault property, azure vault not configured");
                }
                return new AzureVaultPropertyValue(azureVaultProvider, expValue, secure);
            } else if (expType.equals("uuid")) {
                return new UUIDPropertyValue(secure, expValue);
            } else {
                throw new PropertyResolutionException("Invalid property expression type " + expType);
            }
        } else {
            return new DefaultPropertyValue(value, secure);
        }
    }

    @NotNull
    private DefaultPropertyValue processEncryptedException(boolean secure, String expValue) {
        if (decryptionKey == null) {
            throw new PropertyResolutionException("Unable to decrypt value, no crypto key found");
        }
        try {
            return new DefaultPropertyValue(CryptoUtils.decrypt(decryptionKey, expValue), secure);
        } catch (DecryptionException e) {
            throw new PropertyResolutionException("Failed to decrypt encrypted property", e);
        }
    }
}
