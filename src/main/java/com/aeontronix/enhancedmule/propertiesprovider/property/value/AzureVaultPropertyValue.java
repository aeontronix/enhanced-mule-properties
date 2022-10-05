package com.aeontronix.enhancedmule.propertiesprovider.property.value;

import com.aeontronix.commons.StringUtils;
import com.aeontronix.enhancedmule.propertiesprovider.azurevault.AzureVaultProvider;
import com.aeontronix.enhancedmule.propertiesprovider.property.PropertyResolutionException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AzureVaultPropertyValue extends DynamicPropertyValue {
    public static final String AZVAULT_EXPR = "^(secret|cert)(@(.*?))?:(.*)$";
    public static final String SECRET = "secret";
    public static final String CERT = "cert";
    public static final String PCERT = "pcert";
    private final String subType;
    private final String vaultUri;
    private final String key;
    private Pattern vaultValuePattern = Pattern.compile(AZVAULT_EXPR);
    private AzureVaultProvider azureVaultProvider;

    public AzureVaultPropertyValue(AzureVaultProvider azureVaultProvider, String value, boolean secure) {
        super(value, secure, azureVaultProvider.getCacheTtl());
        this.azureVaultProvider = azureVaultProvider;
        final Matcher matcher = vaultValuePattern.matcher(value);
        if (!matcher.find()) {
            throw new PropertyResolutionException("Invalid azvault expression, must match regex " + AZVAULT_EXPR);
        }
        subType = matcher.group(1).toLowerCase();
        vaultUri = matcher.group(3);
        key = matcher.group(4);
    }

    @Override
    protected String evaluateDynamically() {
        switch (subType) {
            case SECRET:
                return azureVaultProvider.findSecret(vaultUri, key);
            case CERT:
                return StringUtils.base64Encode(azureVaultProvider.findKeystore(vaultUri, key));
            case PCERT:
                return StringUtils.base64Encode(azureVaultProvider.findCert(vaultUri, key));
            default:
                throw new PropertyResolutionException("Invalid azvault expression: " + value);
        }
    }

}
