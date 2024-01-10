package org.secure.utilsCrypted.generator;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Locale;

public class CertificateGenerator {
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048,new SecureRandom());

        return generator.generateKeyPair();
    }

    public static X509Certificate generateSelfSignedCertificate(String CommonName,KeyPair keyPair) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException {
        return generateSelfSignedCertificate(CommonName,"Default",keyPair);
    }

    public static X509Certificate generateSelfSignedCertificate(String CommonName,String Organization,KeyPair keyPair) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException {
        return generateSelfSignedCertificate(CommonName,Organization, Locale.getDefault().getCountry(),keyPair);
    }

    public static X509Certificate generateSelfSignedCertificate(String CommonName,String Organization,String Country,KeyPair keyPair) throws NoSuchAlgorithmException, NoSuchProviderException, CertificateException, OperatorCreationException {
        Security.addProvider(new BouncyCastleProvider());

        X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        nameBuilder.addRDN(BCStyle.CN, CommonName);
        nameBuilder.addRDN(BCStyle.O, Organization);
        nameBuilder.addRDN(BCStyle.C, Country);

        X500Name issuerAndSubject = nameBuilder.build();

        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 365 * 24 * 60 * 60 * 1000L); // 1 year validity

        X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(issuerAndSubject,serial,startDate,endDate,issuerAndSubject,keyPair.getPublic());
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA").setProvider("BC").build(keyPair.getPrivate());

        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certificateBuilder.build(signer));
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, CertificateException, OperatorCreationException {
        KeyPair keyPair = generateKeyPair();
        X509Certificate certificate = generateSelfSignedCertificate("Client",keyPair);
        System.out.println(certificate);

        System.out.println();
        X509Certificate cert = CertificateGenerator.generateSelfSignedCertificate("Mael",keyPair);
        System.out.println("Classe instanciée : " + cert.getClass().getName());
        System.out.println("Type de certificat : " + cert.getType());
        System.out.println("Nom du propriétaire du certificat : " + cert.getSubjectDN().getName());

        PublicKey clePublique = cert.getPublicKey();
        System.out.println("... sa clé publique : " + clePublique.toString());
        System.out.println("... la classe instanciée par celle-ci : " + clePublique.getClass().getName());
        System.out.println("Dates limites de validité : [" + cert.getNotBefore() + " - " + cert.getNotAfter() + "]");
        System.out.println("Signataire du certificat : " + cert.getIssuerDN().getName());
        System.out.println("Algo de signature : " + cert.getSigAlgName());
        System.out.println("Signature : " + cert.getSignature());
        System.out.println();

        try{
            cert.verify(cert.getPublicKey());
            System.out.println("\033[92mLa signature est valide. Le certificat est auto-signé.\033[0m");
        } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException |
                 SignatureException e) {
            System.out.println("\033[91msignature auto-signée -> " + e.getMessage() + "\033[0m");
        }
    }
}
