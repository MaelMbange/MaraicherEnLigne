package org.secure.utilsCrypted.requetes;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.secure.utils.interfaces.Requete;
import org.secure.utilsCrypted.generator.CertificateGenerator;
import org.secure.utilsCrypted.generator.Crypt;
import org.secure.utilsCrypted.generator.SessionKeyGenerator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;

public class sRequeteGetFactures implements Requete {
    private byte[] idClient;
    private byte[] signature;

    public sRequeteGetFactures(int idClient, SecretKey sessionKey, PrivateKey privateKey) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException, SignatureException {
        Security.addProvider(new BouncyCastleProvider());

        byte[] data;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            try(DataOutputStream dos = new DataOutputStream(baos)){
                dos.writeInt(idClient);
            }
            data = baos.toByteArray();
        }
        //Crypter le champs idClient avec la cle symmetrique
        this.idClient = Crypt.CryptSymAES(sessionKey,data);

        //Faire la signature avec la l'id de la cle dedans.
        Signature sign = Signature.getInstance("SHA256withRSA","BC");
        sign.initSign(privateKey);
        sign.update(data);
        this.signature = sign.sign();
    }

    public boolean VerifySignature(SecretKey sessionKey, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, SignatureException {
        Security.addProvider(new BouncyCastleProvider());

        Signature sign = Signature.getInstance("SHA256withRSA","BC");
        sign.initVerify(publicKey);
        sign.update(getByteIdClient(sessionKey));

        return sign.verify(signature);
    }

    public byte[] getByteIdClient(SecretKey sessionKey) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        return Crypt.DecryptSymAES(sessionKey,idClient);
    }

    public int getIdClient(SecretKey sessionKey) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException, IOException {
        byte[] data = Crypt.DecryptSymAES(sessionKey,idClient);
        try(ByteArrayInputStream baos = new ByteArrayInputStream(data)){
            try(DataInputStream dos = new DataInputStream(baos)){
                return dos.readInt();
            }
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, IOException, BadPaddingException, SignatureException, InvalidKeyException {
        KeyPair pairCle = CertificateGenerator.generateKeyPair();
        SecretKey session = SessionKeyGenerator.generateKey("AES");

        sRequeteGetFactures rgf = new sRequeteGetFactures(1,session, pairCle.getPrivate());

        System.out.println("Signature valide -> " + rgf.VerifySignature(session, pairCle.getPublic()));
        System.out.println("IdClient -> " + rgf.getIdClient(session));
    }
}
