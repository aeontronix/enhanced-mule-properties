package com.aeontronix.enhancedmule.propertiesprovider.property.value;

import com.aeontronix.commons.DataUtils;
import com.aeontronix.commons.UUIDFactory;
import com.aeontronix.commons.exception.UnexpectedException;
import com.aeontronix.commons.file.FileUtils;
import com.aeontronix.commons.file.TempFile;
import com.aeontronix.commons.io.IOUtils;
import com.aeontronix.enhancedmule.propertiesprovider.property.PropertyResolutionException;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

public class SelfSignedCertificatePropertyValue extends PropertyValue {
    private final PropertyValue dn;
    private final String algorithm;
    private final String signAlgorithm;
    private final int keySize;
    private char[] storePassword;
    private char[] keyPassword;
    private String alias = "cert";
    protected TempFile certFile;
    private byte[] keyStoreData;

    public SelfSignedCertificatePropertyValue(PropertyValue dn, String algorithm, String signAlgorithm, int keySize) {
        this.dn = dn;
        this.algorithm = algorithm;
        this.signAlgorithm = signAlgorithm;
        this.keySize = keySize;
    }

    @Override
    public boolean isSecure() {
        return true;
    }

    @Override
    public String getRawValue() {
        return "[AUTO GENERATED KEYSTORE PATH]";
    }

    public char[] getStorePassword() {
        createCert();
        return storePassword;
    }

    public char[] getKeyPassword() {
        createCert();
        return keyPassword;
    }

    public String getAlias() {
        createCert();
        return alias;
    }

    public void createCert() throws PropertyResolutionException {
        if (certFile == null) {
            try {
                storePassword = DataUtils.uuidToB32Str(UUIDFactory.generate()).toCharArray();
                keyPassword = DataUtils.uuidToB32Str(UUIDFactory.generate()).toCharArray();
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
                keyPairGenerator.initialize(keySize, new SecureRandom());
                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                final X500Name name;
                if( dn != null ) {
                    name = new X500Name(dn.evaluate());
                } else {
                    name = new X500Name("CN=api");
                }
                ContentSigner signer = new JcaContentSignerBuilder(signAlgorithm).build(keyPair.getPrivate());
                final X509CertificateHolder certHolder = new JcaX509v3CertificateBuilder(name,
                        BigInteger.valueOf(System.currentTimeMillis()), new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24),
                        new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * 10)), name, keyPair.getPublic()).build(signer);
                X509Certificate cert = new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(certHolder);
                final ByteArrayOutputStream buf = new ByteArrayOutputStream();
                KeyStore keyStore = KeyStore.getInstance("JKS");
                keyStore.load(null, null);
                keyStore.setKeyEntry(alias, keyPair.getPrivate(), keyPassword, new java.security.cert.Certificate[]{cert});
                keyStore.store(buf, storePassword);
                keyStoreData = buf.toByteArray();
            } catch (NoSuchAlgorithmException | IOException | KeyStoreException | CertificateException |
                     OperatorCreationException e1) {
                throw new PropertyResolutionException("Failed to generate keystore", e1);
            }
            try {
                certFile = new TempFile("prop");
                FileUtils.write(certFile, keyStoreData);
            } catch (IOException e) {
                throw new UnexpectedException(e);
            }
        }
    }

    @Override
    public synchronized String evaluate() throws PropertyResolutionException {
        if( certFile == null ) {
            createCert();
        }
        return certFile.getAbsolutePath();
    }

    @Override
    public synchronized void close() throws IOException {
        deleteCert();
    }

    private synchronized void deleteCert() {
        if( certFile != null ) {
            IOUtils.close(certFile);
            certFile = null;
        }
    }
}
