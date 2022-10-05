package com.aeontronix.enhancedmule.propertiesprovider.azurevault;

import com.aeontronix.commons.StringUtils;
import com.aeontronix.enhancedmule.propertiesprovider.property.PropertyResolutionException;
import com.azure.core.exception.ResourceNotFoundException;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.security.keyvault.certificates.CertificateClient;
import com.azure.security.keyvault.certificates.CertificateClientBuilder;
import com.azure.security.keyvault.certificates.models.KeyVaultCertificateWithPolicy;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AzureVaultProvider {
    private final Map<String, SecretClient> secretClients = new HashMap<>();
    private final Map<String, CertificateClient> certificateClients = new LinkedHashMap<>();
    private final ClientSecretCredential credential;
    private Duration cacheTtl;
    private final String defaultVaultUri;

    public AzureVaultProvider(Duration cacheTtl, String vaultUri, String clientId, String clientSecret, String tenantId) {
        this.cacheTtl = cacheTtl;
        defaultVaultUri = vaultUri;
        credential = new ClientSecretCredentialBuilder()
                .clientId(clientId).clientSecret(clientSecret)
                .tenantId(tenantId).build();
    }

    @NotNull
    private synchronized CertificateClient getCertClient(@Nullable String vaultUri) {
        return certificateClients.computeIfAbsent(vaultUri, v ->
                new CertificateClientBuilder().vaultUrl(convertVaultUri(v)).credential(credential).buildClient());
    }

    @NotNull
    private synchronized SecretClient getSecretsClient(@Nullable String vaultUri) {
        return secretClients.computeIfAbsent(vaultUri, v ->
                new SecretClientBuilder().vaultUrl(convertVaultUri(v)).credential(credential).buildClient());
    }

    public String findSecret(String key) {
        return findSecret(null, key);
    }

    public String findSecret(String vaultUri, String key) {
        final KeyVaultSecret secret;
        try {
            secret = getSecretsClient(vaultUri).getSecret(key);
        } catch (ResourceNotFoundException e) {
            throw new PropertyResolutionException("Couldn't find vault secret " + key + " : " + e.getMessage());
        }
        return secret.getValue();
    }

    public byte[] findCert(@NotNull String key) {
        return findCert(null, key);
    }

    public byte[] findCert(@Nullable String vaultUrl, @NotNull String key) {
        try {
            final KeyVaultCertificateWithPolicy certificate = getCertClient(vaultUrl).getCertificate(key);
            return certificate.getCer();
        } catch (ResourceNotFoundException e) {
            throw new PropertyResolutionException("Couldn't find cert " + key + " : " + e.getMessage());
        }
    }

    public byte[] findKeystore(@NotNull String key) {
        return findKeystore(null, key);

    }

    public byte[] findKeystore(@Nullable String vaultUrl, @NotNull String key) {
        try {
            final String v = getSecretsClient(vaultUrl).getSecret(key).getValue();
            final ByteArrayInputStream is = new ByteArrayInputStream(StringUtils.base64Decode(v));
            KeyStore p12Store = KeyStore.getInstance("PKCS12");
            p12Store.load(is, null);
            final byte[] cert = getCertClient(vaultUrl).getCertificate(key).getCer();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final Certificate certificate = cf.generateCertificate(new ByteArrayInputStream(cert));
            final String alias = p12Store.aliases().nextElement();
            final Key pkey = p12Store.getKey(alias, new char[0]);
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null, null);
            final char[] pw = "secret".toCharArray();
            keyStore.setKeyEntry("cert", pkey, pw, new Certificate[]{certificate});
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            keyStore.store(os, pw);
            os.close();
            return os.toByteArray();
        } catch (ResourceNotFoundException e) {
            throw new PropertyResolutionException("Couldn't find cert " + key + " : " + e.getMessage());
        } catch (UnrecoverableKeyException e) {
            throw new PropertyResolutionException("Private key is not recoverable: " + e.getMessage(), e);
        } catch (CertificateException | KeyStoreException | NoSuchAlgorithmException | IOException e) {
            throw new PropertyResolutionException(e.getMessage(), e);
        }
    }

    @NotNull
    private String convertVaultUri(@Nullable String vaultUri) {
        if( vaultUri == null ) {
            vaultUri = defaultVaultUri;
        }
        if (!vaultUri.toLowerCase().startsWith("https://")) {
            return "https://" + vaultUri + ".vault.azure.net/";
        } else {
            return vaultUri;
        }
    }

    public Duration getCacheTtl() {
        return cacheTtl;
    }
}
