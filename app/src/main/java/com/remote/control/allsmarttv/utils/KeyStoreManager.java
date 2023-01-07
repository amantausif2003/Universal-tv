package com.remote.control.allsmarttv.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.UUID;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;


public final class KeyStoreManager {
    private static String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String KEYSTORE_FILENAME = "atvremote.keystore";
    private static final char[] KEYSTORE_PASSWORD = "KeyStore_Password".toCharArray();
    private static final String LOCAL_IDENTITY_ALIAS = "atvremote-remote";
    private static final String REMOTE_IDENTITY_ALIAS_PATTERN = "atvremote-remote-%s";
    private static final String SERVER_IDENTITY_ALIAS = "atvremote-local";
    private static final String TAG = "KeyStoreManager";
    private final Context mContext;
    private final DynamicTrustManager mDynamicTrustManager = new DynamicTrustManager(this.mKeyStore);
    private final KeyStore mKeyStore = load();


    public static class DynamicTrustManager implements X509TrustManager {
        private X509TrustManager trustManager;

        public DynamicTrustManager(KeyStore keyStore) {
            reloadTrustManager(keyStore);
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        public void reloadTrustManager(KeyStore keyStore) {
            try {
                TrustManagerFactory instance = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                instance.init(keyStore);
                TrustManager[] trustManagers = instance.getTrustManagers();
                for (int i = 0; i < trustManagers.length; i++) {
                    if (trustManagers[i] instanceof X509TrustManager) {
                        this.trustManager = (X509TrustManager) trustManagers[i];
                        return;
                    }
                }
                throw new IllegalStateException("No trust manager found");
            } catch (KeyStoreException | NoSuchAlgorithmException e) {
            }
        }
    }

    public KeyStoreManager(Context context) {
        this.mContext = context;
    }

    private void clearKeyStore() {
        try {
            Enumeration<String> aliases = this.mKeyStore.aliases();
            while (aliases.hasMoreElements()) {
                this.mKeyStore.deleteEntry(aliases.nextElement());
            }
        } catch (KeyStoreException e) {
        }
        store();
    }

    private static String createAlias(String str) {
        return String.format(REMOTE_IDENTITY_ALIAS_PATTERN, str);
    }

    private void createIdentity(KeyStore keyStore) throws GeneralSecurityException {
        createIdentity(keyStore, SERVER_IDENTITY_ALIAS);
    }

    private void createIdentity(KeyStore keyStore, String str) throws GeneralSecurityException {
        createIdentity(keyStore, str, getUniqueId());
    }

    private void createIdentity(KeyStore keyStore, String str, String str2) throws GeneralSecurityException {
        KeyPair generateKeyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        keyStore.setKeyEntry(str, generateKeyPair.getPrivate(), null, new Certificate[]{SslCertUtil.generateX509V3Certificate(generateKeyPair, getCertificateName(str2))});
    }

    private KeyStore createIdentityKeyStore() throws GeneralSecurityException {
        KeyStore instance;
        if (!useAndroidKeyStore()) {
            instance = KeyStore.getInstance(KeyStore.getDefaultType());
            try {
                instance.load(null, KEYSTORE_PASSWORD);
            } catch (IOException e) {
                throw new GeneralSecurityException("Unable to create empty keyStore", e);
            }
        } else {
            instance = KeyStore.getInstance("AndroidKeyStore");
            try {
                instance.load(null);
            } catch (IOException e2) {
                throw new GeneralSecurityException("Unable to create empty keyStore", e2);
            }
        }
        createIdentity(instance);
        return instance;
    }

    private static final String getCertificateName() {
        return getCertificateName(getUniqueId());
    }

    private static final String getCertificateName(String str) {
        return "CN=atvremote/" + Build.PRODUCT + "/" + Build.DEVICE + "/" + Build.MODEL + "/" + str;
    }

    private static String getSubjectDN(Certificate certificate) {
        X500Principal subjectX500Principal;
        if ((certificate instanceof X509Certificate) && (subjectX500Principal = ((X509Certificate) certificate).getSubjectX500Principal()) != null) {
            return subjectX500Principal.getName();
        }
        return null;
    }

    private static final String getUniqueId() {
        if (!TextUtils.isEmpty(Build.SERIAL)) {
            return Build.SERIAL;
        }
        String address = BluetoothAdapter.getDefaultAdapter().getAddress();
        return TextUtils.isEmpty(address) ? UUID.randomUUID().toString() : address;
    }

    private boolean hasServerIdentityAlias(KeyStore keyStore) {
        try {
            return keyStore.containsAlias(SERVER_IDENTITY_ALIAS);
        } catch (KeyStoreException e) {
            return false;
        }
    }

    private KeyStore load() {
        KeyStore keyStore;
        try {
            if (!useAndroidKeyStore()) {
                keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(this.mContext.openFileInput(KEYSTORE_FILENAME), KEYSTORE_PASSWORD);
            } else {
                keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
                keyStore.load(null);
            }
        } catch (KeyStoreException e) {
            throw new IllegalStateException("Unable to get default instance of KeyStore", e);
        } catch (IOException | GeneralSecurityException e2) {
            keyStore = null;
        }
        if (keyStore != null && hasServerIdentityAlias(keyStore)) {
            return keyStore;
        }
        try {
            KeyStore createIdentityKeyStore = createIdentityKeyStore();
            store(createIdentityKeyStore);
            return createIdentityKeyStore;
        } catch (GeneralSecurityException e3) {
            throw new IllegalStateException("Unable to create identity KeyStore", e3);
        }
    }

    private void store(KeyStore keyStore) {
        if (!useAndroidKeyStore()) {
            try {
                FileOutputStream openFileOutput = this.mContext.openFileOutput(KEYSTORE_FILENAME, 0);
                keyStore.store(openFileOutput, KEYSTORE_PASSWORD);
                openFileOutput.close();
            } catch (IOException e) {
                throw new IllegalStateException("Unable to store keyStore", e);
            } catch (GeneralSecurityException e2) {
                throw new IllegalStateException("Unable to store keyStore", e2);
            }
        }
    }

    private boolean useAndroidKeyStore() {
        return Build.VERSION.SDK_INT >= 18;
    }

    public void clear() {
        clearKeyStore();
        try {
            createIdentity(this.mKeyStore);
        } catch (GeneralSecurityException e) {
        }
        store();
    }

    public KeyManager[] getKeyManagers() throws GeneralSecurityException {
        KeyManagerFactory instance = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        instance.init(this.mKeyStore, "".toCharArray());
        return instance.getKeyManagers();
    }

    public TrustManager[] getTrustManagers() throws GeneralSecurityException {
        try {
            return new DynamicTrustManager[]{this.mDynamicTrustManager};
        } catch (Exception e) {
            throw new GeneralSecurityException(e);
        }
    }

    public boolean hasServerIdentityAlias() {
        return hasServerIdentityAlias(this.mKeyStore);
    }

    public void initializeKeyStore() {
        initializeKeyStore(getUniqueId());
    }

    public void initializeKeyStore(String str) {
        clearKeyStore();
        try {
            createIdentity(this.mKeyStore, LOCAL_IDENTITY_ALIAS, str);
            store();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to create identity KeyStore", e);
        }
    }

    public Certificate removeCertificate(String str) {
        try {
            String createAlias = createAlias(str);
            if (!this.mKeyStore.containsAlias(createAlias)) {
                return null;
            }
            Certificate certificate = this.mKeyStore.getCertificate(createAlias);
            this.mKeyStore.deleteEntry(createAlias);
            store();
            return certificate;
        } catch (KeyStoreException e) {
            return null;
        }
    }

    public void store() {
        this.mDynamicTrustManager.reloadTrustManager(this.mKeyStore);
        store(this.mKeyStore);
    }

    public void storeCertificate(Certificate certificate) {
        storeCertificate(certificate, Integer.toString(certificate.hashCode()));
    }

    public void storeCertificate(Certificate certificate, String str) {
        try {
            String createAlias = createAlias(str);
            String subjectDN = getSubjectDN(certificate);
            if (this.mKeyStore.containsAlias(createAlias)) {
                this.mKeyStore.deleteEntry(createAlias);
            }
            if (subjectDN != null) {
                Enumeration<String> aliases = this.mKeyStore.aliases();
                while (aliases.hasMoreElements()) {
                    String nextElement = aliases.nextElement();
                    String subjectDN2 = getSubjectDN(this.mKeyStore.getCertificate(nextElement));
                    if (subjectDN2 != null && subjectDN2.equals(subjectDN)) {
                        Log.d(TAG, "Deleting entry for " + nextElement + " (" + subjectDN2 + ")");
                        this.mKeyStore.deleteEntry(nextElement);
                    }
                }
            }
            this.mKeyStore.setCertificateEntry(createAlias, certificate);
            store();
        } catch (KeyStoreException e) {
        }
    }
}
