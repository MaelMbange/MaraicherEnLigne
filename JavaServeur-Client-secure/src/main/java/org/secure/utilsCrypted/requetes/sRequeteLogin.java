package org.secure.utilsCrypted.requetes;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.secure.utilsCrypted.generator.CertificateGenerator;
import org.secure.utils.interfaces.Requete;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Random;

public class sRequeteLogin implements Requete {
    private String login;
    private int random;
    private long time;
    private byte[] digest;
    private X509Certificate certificate;

    public sRequeteLogin(String login, String password, X509Certificate certificate) throws NoSuchAlgorithmException, IOException, CertificateEncodingException {
        this.login = login;
        this.time = Instant.now().toEpochMilli();
        this.random = new Random().nextInt();
        this.certificate = certificate;

        byte[] sel;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            try(DataOutputStream dos = new DataOutputStream(baos)){
                dos.writeInt(this.random);
                dos.writeLong(this.time);
            }
            sel = baos.toByteArray();
        }

        // Creation du digest
        Security.addProvider(new BouncyCastleProvider());
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        messageDigest.update(this.login.getBytes());
        messageDigest.update(password.getBytes());
        messageDigest.update(this.certificate.getEncoded());
        messageDigest.update(sel);

        this.digest = messageDigest.digest();
    }

    public String getLogin(){
        return this.login;
    }

    public X509Certificate getCertificate(){return this.certificate;}


     /**
     * A function that will tell you if the password is ok or not by recreating the digest.
     * @param password
     * @return True or False
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws CertificateEncodingException
     */
    public boolean VerifyPassword(String password) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, CertificateEncodingException {

        byte[] sel;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            try(DataOutputStream dos = new DataOutputStream(baos)){
                dos.writeInt(this.random);
                dos.writeLong(this.time);
            }
            sel = baos.toByteArray();
        }

        Security.addProvider(new BouncyCastleProvider());
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        messageDigest.update(this.login.getBytes());
        messageDigest.update(password.getBytes());
        messageDigest.update(this.certificate.getEncoded());
        messageDigest.update(sel);

        return MessageDigest.isEqual(messageDigest.digest(),digest);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, CertificateException, OperatorCreationException, IOException {
        KeyPair keyPair = CertificateGenerator.generateKeyPair();
        X509Certificate certificate = CertificateGenerator.generateSelfSignedCertificate("Mael",keyPair);

        sRequeteLogin r = new sRequeteLogin("Mael","Halo",certificate);

        System.out.println("verification = " + r.VerifyPassword("Halo"));
    }
}
