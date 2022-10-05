package com.aeontronix.enhancedmule.propertiesprovider;

import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.runtime.api.meta.model.declaration.fluent.ConfigurationDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer;
import org.mule.runtime.api.meta.model.display.DisplayModel;
import org.mule.runtime.extension.api.loader.ExtensionLoadingContext;
import org.mule.runtime.extension.api.loader.ExtensionLoadingDelegate;

import static org.mule.metadata.api.model.MetadataFormat.JAVA;
import static org.mule.runtime.api.meta.Category.COMMUNITY;
import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

public class EConfigExtensionLoadingDelegate implements ExtensionLoadingDelegate {
    public static final String EXTENSION_NAME = "Enhanced Mule Properties";
    public static final String CONFIG_ELEMENT = "config";
    public static final String DEF_CRYPTOKEY = "sys::em.config.key";
    public static final String DEF_ENV_TYPE = "sys::anypoint.env.type:local";
    public static final String DEF_ENV_NAME = "sys::anypoint.env.name:local";
    public static final String DEF_DESCPATH = "properties.yaml";
    public static final String DEF_SERVER_ADDRESS = "sys::em.server.address";
    public static final String DEF_SERVER_KEY = "sys::em.server.key";
    public static final String DEF_SERVER_ENABLED = "sys::em.server.enabled:false";
    public static final String DEF_LOGPROPS = "sys::em.properties.log:true";
    public static final String DEF_APP_NAME = "sys::app.name";
    public static final String DESC_PATH_PARAM = "descPath";
    public static final String ENV_NAME_PARAM = "envNameProperty";
    public static final String ENV_TYPE_PARAM = "envTypeProperty";
    public static final String LOG_RESOLVED_PARAM = "logResolvedProperty";
    public static final String APP_NAME_PROPERTY = "appNameProperty";
    public static final String CRYPTO_KEY_PARAM = "cryptoKeyProperty";
    public static final String AZURE_VAULT = "Azure Vault";
    public static final String AZURE_VAULT_ENABLED_PARAM = "azureVaultEnabled";
    public static final String DEF_AZVAULT_ENABLED = "sys::em.azure.vault.enabled:false";
    public static final String AZURE_VAULT_CLIENT_ID_PARAM = "azureVaultClientId";
    public static final String DEF_AZVAULT_CLIENT_ID = "sys::em.azure.client.id";
    public static final String AZURE_VAULT_CLIENT_SECRET_PARAM = "azureVaultClientSecret";
    public static final String DEF_AZVAULT_CLIENT_SECRET = "sys::em.azure.client.secret";
    public static final String AZURE_VAULT_TENANT_ID_PARAM = "azureVaultTenantId";
    public static final String DEF_AZVAULT_TENANT_ID = "sys::em.azure.tenant.id";
    public static final String AZURE_VAULT_URL_PARAM = "azureVaultURl";
    public static final String DEF_AZVAULT_URL = "sys::em.azure.vault.url";
    public static final String PROPERTIES_VALIDATION = "propertiesValidation";
    public static final String AZURE_VAULT_CACHE_TTL = "azVaultCacheTtl";
    public static final String DEF_CACHE_TTL = "PT10M";

    @Override
    public void accept(ExtensionDeclarer extensionDeclarer, ExtensionLoadingContext context) {
        final ExtensionDeclarer extDec = extensionDeclarer.named(EXTENSION_NAME)
                .describedAs(String.format("Crafted %s Extension", EXTENSION_NAME))
                .withCategory(COMMUNITY)
                .onVersion("1.0.0")
                .fromVendor("Aeontronix");
        ConfigurationDeclarer configurationDeclarer = extDec.withConfig(CONFIG_ELEMENT);
        ParameterGroupDeclarer core = configurationDeclarer
                .onParameterGroup("Core");
        core
                .withOptionalParameter(DESC_PATH_PARAM)
                .withDisplayModel(DisplayModel.builder().displayName("Descriptor path").build())
                .describedAs("Enhanced Mule configuration file path")
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(NOT_SUPPORTED)
                .defaultingTo(DEF_DESCPATH);
        core
                .withOptionalParameter(ENV_NAME_PARAM)
                .withDisplayModel(DisplayModel.builder().displayName("Environment name").build())
                .describedAs("Environment name")
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(NOT_SUPPORTED)
                .defaultingTo(DEF_ENV_NAME);
        core
                .withOptionalParameter(ENV_TYPE_PARAM)
                .withDisplayModel(DisplayModel.builder().displayName("Environment type").build())
                .describedAs("Environment type")
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(NOT_SUPPORTED)
                .defaultingTo(DEF_ENV_TYPE);
        core
                .withOptionalParameter(CRYPTO_KEY_PARAM)
                .withDisplayModel(DisplayModel.builder().displayName("Crypto key").build())
                .describedAs("encryption key")
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(NOT_SUPPORTED)
                .defaultingTo(DEF_CRYPTOKEY);
        core
                .withOptionalParameter(LOG_RESOLVED_PARAM)
                .withDisplayModel(DisplayModel.builder().displayName("Log resolved properties").build())
                .describedAs("Log resolved properties (except for sensitive properties)")
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(NOT_SUPPORTED)
                .defaultingTo(DEF_LOGPROPS);
        core
                .withOptionalParameter(APP_NAME_PROPERTY)
                .withDisplayModel(DisplayModel.builder().displayName("Application Name").build())
                .describedAs("Application name")
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(NOT_SUPPORTED)
                .defaultingTo(DEF_APP_NAME);
        core
                .withOptionalParameter(PROPERTIES_VALIDATION)
                .withDisplayModel(DisplayModel.builder().displayName("Validate properties").build())
                .describedAs("Validate properties as per definitions.")
                .ofType(BaseTypeBuilder.create(JAVA).booleanType().build())
                .withExpressionSupport(NOT_SUPPORTED)
                .defaultingTo("true");
        ParameterGroupDeclarer azureVault = configurationDeclarer
                .onParameterGroup(AZURE_VAULT);
        azureVault
                .withOptionalParameter(AZURE_VAULT_ENABLED_PARAM)
                .withDisplayModel(DisplayModel.builder().displayName("Azure Vault Enabled Property").build())
                .describedAs("Enable azure vault integration")
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(NOT_SUPPORTED)
                .defaultingTo(DEF_AZVAULT_ENABLED);
        azureVault
                .withOptionalParameter(AZURE_VAULT_CACHE_TTL)
                .withDisplayModel(DisplayModel.builder().displayName("Cache Time to live").build())
                .describedAs("Duration (in ISO 8601 duration format) for which retrieve vault data is cached")
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(NOT_SUPPORTED)
                .defaultingTo(DEF_CACHE_TTL);
        azureVault
                .withOptionalParameter(AZURE_VAULT_TENANT_ID_PARAM)
                .withDisplayModel(DisplayModel.builder().displayName("Tenant Id").build())
                .describedAs("Azure vault tenant id")
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(NOT_SUPPORTED)
                .defaultingTo(DEF_AZVAULT_TENANT_ID);
        azureVault
                .withOptionalParameter(AZURE_VAULT_URL_PARAM)
                .withDisplayModel(DisplayModel.builder().displayName("Vault URL").build())
                .describedAs("Azure vault url")
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(NOT_SUPPORTED)
                .defaultingTo(DEF_AZVAULT_URL);
        azureVault
                .withOptionalParameter(AZURE_VAULT_CLIENT_ID_PARAM)
                .withDisplayModel(DisplayModel.builder().displayName("Azure Vault Client Id").build())
                .describedAs("Azure service principal client id")
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(NOT_SUPPORTED)
                .defaultingTo(DEF_AZVAULT_CLIENT_ID);
        azureVault
                .withOptionalParameter(AZURE_VAULT_CLIENT_SECRET_PARAM)
                .withDisplayModel(DisplayModel.builder().displayName("Azure Vault Client Secret").build())
                .describedAs("Azure service principal client secret")
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(NOT_SUPPORTED)
                .defaultingTo(DEF_AZVAULT_CLIENT_SECRET);
    }

}
