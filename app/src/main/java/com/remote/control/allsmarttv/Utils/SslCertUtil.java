package com.remote.control.allsmarttv.Utils;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

public class SslCertUtil {

        public static AuthorityKeyIdentifier authorityKeyIdentifier(PublicKey publicKey, X509Name x509Name, BigInteger bigInteger) {
            GeneralName generalName = new GeneralName(x509Name);
            try {
                return new AuthorityKeyIdentifier(new SubjectPublicKeyInfo((ASN1Sequence) new ASN1InputStream(publicKey.getEncoded()).readObject()), new GeneralNames(generalName), bigInteger);
            } catch (IOException unused) {
                throw new RuntimeException("Error encoding public key");
            }
        }

        public static X509Certificate generateX509V3Certificate(KeyPair keyPair, String str) throws CertificateEncodingException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Calendar instance = Calendar.getInstance();
        instance.set(2009, 0, 1);
        Date date = new Date(instance.getTimeInMillis());
        instance.set(2099, 0, 1);
        return generateX509V3Certificate(keyPair, str, date, new Date(instance.getTimeInMillis()), BigInteger.valueOf(Math.abs(System.currentTimeMillis())));
        }


        public static X509Certificate generateX509V3Certificate(KeyPair keyPair, String str, Date date, Date date2, BigInteger bigInteger) throws CertificateEncodingException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
            X509V3CertificateGenerator x509V3CertificateGenerator = new X509V3CertificateGenerator();
            X509Name x509Name = new X509Name(str);
            x509V3CertificateGenerator.setSerialNumber(bigInteger);
            x509V3CertificateGenerator.setIssuerDN(x509Name);
            x509V3CertificateGenerator.setSubjectDN(x509Name);
            x509V3CertificateGenerator.setNotBefore(date);
            x509V3CertificateGenerator.setNotAfter(date2);
            x509V3CertificateGenerator.setPublicKey(keyPair.getPublic());
            x509V3CertificateGenerator.setSignatureAlgorithm("SHA256WithRSAEncryption");
            x509V3CertificateGenerator.addExtension(X509Extensions.BasicConstraints, true, (ASN1Encodable) new BasicConstraints(false));
            x509V3CertificateGenerator.addExtension(X509Extensions.KeyUsage, true, (ASN1Encodable) new KeyUsage((int) 164));
            x509V3CertificateGenerator.addExtension(X509Extensions.ExtendedKeyUsage, true, (ASN1Encodable) new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));
            x509V3CertificateGenerator.addExtension(X509Extensions.AuthorityKeyIdentifier, true, (ASN1Encodable) authorityKeyIdentifier(keyPair.getPublic(), x509Name, bigInteger));
//            x509V3CertificateGenerator.addExtension(X509Extensions.SubjectKeyIdentifier, true, (ASN1Encodable) subjectKeyIdentifier(keyPair.getPublic()));
            x509V3CertificateGenerator.addExtension(X509Extensions.SubjectAlternativeName, false, (ASN1Encodable) new GeneralNames(new GeneralName(1, "android-tv-remote-support@google.com")));
            return x509V3CertificateGenerator.generate(keyPair.getPrivate());
        }


    }


