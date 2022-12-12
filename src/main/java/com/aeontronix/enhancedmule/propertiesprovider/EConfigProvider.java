package com.aeontronix.enhancedmule.propertiesprovider;

import com.aeontronix.commons.StringUtils;
import com.aeontronix.commons.exception.UnexpectedException;
import com.aeontronix.enhancedmule.propertiesprovider.azurevault.AzureVaultProvider;
import com.aeontronix.enhancedmule.propertiesprovider.config.UserConfigFile;
import com.aeontronix.enhancedmule.propertiesprovider.property.ConfigProperties;
import com.aeontronix.enhancedmule.propertiesprovider.property.ConfigProperty;
import com.aeontronix.enhancedmule.propertiesprovider.property.ConfigPropertyDeserializer;
import com.aeontronix.enhancedmule.propertiesprovider.property.PropertyResolutionException;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.DefaultPropertyValue;
import com.aeontronix.enhancedmule.propertiesprovider.property.value.PropertyValue;
import com.aeontronix.enhancedmule.propertiesprovider.utils.JacksonFlattener;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.config.api.dsl.model.ConfigurationParameters;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;
import org.slf4j.Logger;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.aeontronix.enhancedmule.propertiesprovider.EConfigExtensionLoadingDelegate.*;
import static org.slf4j.LoggerFactory.getLogger;

public class EConfigProvider implements ConfigurationPropertiesProvider, Initialisable, Disposable {
    private static final Logger logger = getLogger(EConfigProvider.class);
    private static final String configFilename = "enhanced-mule.config.json";
    private final ResourceProvider externalResourceProvider;
    private final String envNameProperty;
    private final String envTypeProperty;
    private final String descriptorPath;
    private final String cryptoKey;
    private final String logResolvedProperties;
    private final String azVaultEnabled;
    private final String azVaultUrl;
    private final String azVaultTenantId;
    private final String azVaultClientId;
    private final String azVaultClientSecret;
    private final boolean propertiesValidation;
    private final String azVaultCacheTtl;
    private ResolvedProperties resolvedProperties;
    private PropertyResolutionContextImpl propertyResolutionContext;
    private AzureVaultProvider azureVaultProvider;

    public EConfigProvider(ResourceProvider externalResourceProvider, ConfigurationParameters parameters) {
        this.externalResourceProvider = externalResourceProvider;
        cryptoKey = getParam(parameters, CRYPTO_KEY_PARAM, DEF_CRYPTOKEY);
        envNameProperty = getParam(parameters, ENV_NAME_PARAM, DEF_ENV_NAME);
        envTypeProperty = getParam(parameters, ENV_TYPE_PARAM, DEF_ENV_TYPE);
        descriptorPath = getParam(parameters, DESC_PATH_PARAM, DEF_DESCPATH);
        logResolvedProperties = getParam(parameters, LOG_RESOLVED_PARAM, DEF_LOGPROPS);
        azVaultEnabled = getParam(parameters, AZURE_VAULT_ENABLED_PARAM, DEF_AZVAULT_ENABLED);
        azVaultCacheTtl = getParam(parameters, AZURE_VAULT_CACHE_TTL, DEF_CACHE_TTL);
        azVaultTenantId = getParam(parameters, AZURE_VAULT_TENANT_ID_PARAM, DEF_AZVAULT_TENANT_ID);
        azVaultUrl = getParam(parameters, AZURE_VAULT_URL_PARAM, DEF_AZVAULT_URL);
        azVaultClientId = getParam(parameters, AZURE_VAULT_CLIENT_ID_PARAM, DEF_AZVAULT_CLIENT_ID);
        azVaultClientSecret = getParam(parameters, AZURE_VAULT_CLIENT_SECRET_PARAM, DEF_AZVAULT_CLIENT_SECRET);
        propertiesValidation = Boolean.parseBoolean(getParam(parameters, PROPERTIES_VALIDATION, "true"));
    }

    private String getParam(ConfigurationParameters parameters, String key, String def) {
        final String value = parameters.getStringParameter(key);
        if (StringUtils.isBlank(value)) {
            return def;
        } else {
            return value;
        }
    }

    @Override
    public Optional<ConfigurationProperty> getConfigurationProperty(String key) {
        final PropertyValue value = resolvedProperties.get(key);
        if (value != null) {
            return propResult(key, value.evaluate());
        }
        return Optional.empty();
    }

    @Contract("_, _ -> new")
    private @NotNull Optional<ConfigurationProperty> propResult(String key, String value) {
        return Optional.of(new ConfigurationProperty() {
            @Override
            public Object getSource() {
                return "enhanced mule provider source";
            }

            @Override
            public Object getRawValue() {
                return value;
            }

            @Override
            public String getKey() {
                return key;
            }
        });
    }

    @Override
    public String getDescription() {
        return "Enhanced mule properties provider";
    }

    @Override
    public void initialise() throws InitialisationException {
        try {
            final String cryptoKeyOverride = resolveConnectorConfig(cryptoKey);
            String envName = resolveConnectorConfig(envNameProperty).toLowerCase();
            String envType = resolveConnectorConfig(envTypeProperty).toLowerCase();
            logger.info("Loading properties for environment {} of type {}", envName, envType);
            final JsonMapper.Builder builder;
            final String descriptorPathResolved = resolveConnectorConfig(descriptorPath);
            String overrideBasePath;
            final String descPathResLC = descriptorPathResolved.toLowerCase();
            if (descPathResLC.endsWith(".yaml")) {
                overrideBasePath = genBasePath(descriptorPathResolved, 5);
                builder = JsonMapper.builder(new YAMLFactory());
            } else if (descPathResLC.endsWith(".yml")) {
                overrideBasePath = genBasePath(descriptorPathResolved, 4);
                builder = JsonMapper.builder(new YAMLFactory());
            } else if (descPathResLC.endsWith(".json")) {
                overrideBasePath = genBasePath(descriptorPathResolved, 5);
                builder = JsonMapper.builder();
            } else {
                throw new IllegalArgumentException("Invalid descriptor file path, must end in .json, .yaml or .yml");
            }
            final JsonMapper objectMapper = builder
                    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .build();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(ConfigProperty.class, new ConfigPropertyDeserializer(objectMapper));
            objectMapper.registerModule(module);
            final InputStream resourceAsStream = externalResourceProvider.getResourceAsStream(descriptorPathResolved);
            if (resourceAsStream == null) {
                throw new IOException("Application descriptor not found in classpath: " + descriptorPath);
            }
            String cryptoKey = cryptoKeyOverride;
            if (cryptoKey == null) {
                cryptoKey = loadEncryptionKey(objectMapper);
            }
            final ObjectNode tree = (ObjectNode) objectMapper.readTree(resourceAsStream);
            if (logger.isDebugEnabled()) {
                logger.debug("Loaded config tree: " + tree.toPrettyString());
            }
            if (toBoolean(resolveConnectorConfig(this.azVaultEnabled))) {
                logger.info("Azure Vault integration enabled");
                final String url = resolveConnectorConfig(azVaultUrl);
                final String clientId = resolveRequiredConnectorConfig(azVaultClientId);
                final String tenantId = resolveRequiredConnectorConfig(azVaultTenantId);
                logger.info("Azure Vault Cache TTL: " + azVaultCacheTtl);
                logger.info("Azure Vault URL: " + url);
                logger.info("Azure Vault tenant id: " + tenantId);
                logger.info("Azure Vault client Id: " + clientId);
                final Duration cacheDuration = (azVaultCacheTtl.equals("0")
                        || azVaultCacheTtl.equalsIgnoreCase("no")) ? null
                        : Duration.parse(azVaultCacheTtl);
                azureVaultProvider = new AzureVaultProvider(cacheDuration, url, clientId, resolveRequiredConnectorConfig(azVaultClientSecret),
                        tenantId);
            }
            Map<String, String> overrides = findOverrideFile(overrideBasePath, envType, envName);
            propertyResolutionContext = new PropertyResolutionContextImpl(envType, envName, cryptoKey, overrides, azureVaultProvider, propertiesValidation);
            ConfigProperties properties;
            if (tree != null && !tree.isNull()) {
                if (((JsonNode) tree).isObject()) {
                    properties = objectMapper.readerFor(ConfigProperties.class).readValue(tree);
                } else {
                    throw new IOException("Invalid properties json node: " + tree);
                }
            } else {
                properties = new ConfigProperties();
            }
            resolvedProperties = properties.resolveProperties(propertyResolutionContext);
            if (overrides != null) {
                for (Map.Entry<String, String> e : overrides.entrySet()) {
                    if (resolvedProperties.get(e.getKey()) == null) {
                        if (propertiesValidation) {
                            throw new PropertyResolutionException("Found undefined property: " + e.getKey());
                        } else {
                            resolvedProperties.add(e.getKey(), new DefaultPropertyValue(e.getValue(), false));
                        }
                    }
                }
            }
            if (toBoolean(resolveConnectorConfig(logResolvedProperties))) {
                ArrayList<String> resolvedPropertiesLog = new ArrayList<>();
                for (Map.Entry<String, PropertyValue> entry : resolvedProperties.getProperties().entrySet()) {
                    String key = entry.getKey();
                    String value;
                    final PropertyValue propValue = entry.getValue();
                    if (propValue.isSecure()) {
                        value = "******************";
                    } else {
                        value = propValue.evaluate();
                    }
                    resolvedPropertiesLog.add("  " + key + "=" + value);
                }
                logger.info("Resolved Enhanced Mule Properties:\n" + String.join("\n", resolvedPropertiesLog));
            }
            logger.info("Properties initialized");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            if (System.getProperty("org.mule.tooling.runtime.args") == null || System.getProperty("eclipse.launcher") == null) {
                throw new InitialisationException(e, this);
            }
        }
    }

    private Map<String, String> findOverrideFile(String basePath, String envType, String envName) throws IOException {
        if (envType.equalsIgnoreCase("local")) {
            return findOverrideFile(basePath + "-local");
        } else {
            final Map<String, String> overrideFile = findOverrideFile(basePath + "-env-" + envName);
            if (overrideFile != null) {
                return overrideFile;
            } else {
                return findOverrideFile(basePath + "-envtype-" + envType);
            }
        }
    }

    private Map<String, String> findOverrideFile(String basePath) throws IOException {
        Map<String, String> res = findOverridePropertyFile(basePath);
        if (res == null) {
            res = findOverrideJacksonFile(basePath, "yaml");
        }
        if (res == null) {
            res = findOverrideJacksonFile(basePath, "json");
        }
        if (res == null) {
            res = findOverrideJacksonFile(basePath, "yml");
        }
        return res;
    }

    private Map<String, String> findOverridePropertyFile(String basePath) throws IOException {
        try (InputStream is = externalResourceProvider.getResourceAsStream(basePath + ".properties")) {
            if (is != null) {
                final Properties props = new Properties();
                props.load(is);
                return props.entrySet().stream().collect(Collectors
                        .toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));
            } else {
                return null;
            }
        } catch ( MuleRuntimeException e ) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> findOverrideJacksonFile(String basePath, String ext) throws IOException {
        try (InputStream is = externalResourceProvider.getResourceAsStream(basePath + "." + ext)) {
            if (is != null) {
                final ObjectMapper objectMapper;
                if (ext.equals("json")) {
                    objectMapper = new ObjectMapper();
                } else {
                    objectMapper = new ObjectMapper(new YAMLFactory());
                }
                return JacksonFlattener.flattenToStringMap(objectMapper.readValue(is, Map.class));
            } else {
                return null;
            }
        } catch ( MuleRuntimeException e ) {
            return null;
        }
    }

    private String genBasePath(String pathResolved, int len) {
        return pathResolved.substring(0, pathResolved.length() - len);
    }

    private static boolean toBoolean(String val) {
        switch (val.toLowerCase()) {
            case "yes":
            case "true":
                return true;
            case "no":
            case "false":
                return false;
            default:
                throw new IllegalArgumentException("Invalid property should be either yes, true, no, false : " + val);
        }
    }

    private String resolveRequiredConnectorConfig(String value) {
        final String resolved = resolveConnectorConfig(value);
        if (StringUtils.isBlank(resolved)) {
            throw new IllegalArgumentException("Required configuration parameter resolved to blank: " + value);
        }
        return resolved;
    }

    private String resolveConnectorConfig(String value) {
        if (value != null && value.toLowerCase().startsWith("sys::")) {
            value = value.substring(5);
            int defSep = value.indexOf(':');
            String defValue = null;
            if (defSep != -1) {
                try {
                    defValue = value.substring(defSep + 1);
                } catch (StringIndexOutOfBoundsException e) {
                    //
                }
                value = value.substring(0, defSep);
            }
            value = System.getProperty(value);
            return value != null ? value : defValue;
        } else {
            return value;
        }
    }

    private String loadEncryptionKey(ObjectMapper objectMapper) throws IOException {
        logger.debug("Loading encryption key");
        try (final InputStream is = findConfigFile()) {
            if (is != null) {
                try {
                    final UserConfigFile cfg = objectMapper.readValue(is, UserConfigFile.class);
                    if (cfg.getProfiles() != null && cfg.getProfiles().size() == 1) {
                        return cfg.getProfiles().entrySet().iterator().next().getValue().getKey();
                    } else if (cfg.getDefaultProfile() != null) {
                        final UserConfigFile.Profile profile = cfg.getProfiles().get(cfg.getDefaultProfile());
                        if (profile != null) {
                            return profile.getKey();
                        }
                    }
                } catch (IOException e) {
                    throw new IOException("Invalid configuration file", e);
                }
            }
        }
        return null;
    }

    private static InputStream findConfigFile() throws FileNotFoundException {
        File file = new File(configFilename);
        if (file.exists()) {
            logger.debug("Found config file: "+file.getPath());
            return new FileInputStream(file);
        }
        file = new File(System.getProperty("user.home"), "."+configFilename);
        if (file.exists()) {
            logger.debug("Found config file: "+file.getPath());
            return new FileInputStream(file);
        }
        final InputStream is = EConfigProvider.class.getClassLoader().getResourceAsStream(configFilename);
        if( is != null ) {
            logger.debug("Found config file: "+configFilename);
        } else {
            logger.debug("Couldn't find config file");
        }
        return is;
    }

    @Override
    public void dispose() {
        if (resolvedProperties != null) {
            resolvedProperties.getProperties().values().forEach(p -> {
                try {
                    p.close();
                } catch (IOException e) {
                    throw new UnexpectedException(e);
                }
            });
        }
    }
}
