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
import java.util.Arrays;

public class sRequetePayFacture implements Requete {
    private byte[] dataPay;

    public sRequetePayFacture(int idFacture, String nomCarte, String numeroCarte, SecretKey sessionkey) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        Security.addProvider(new BouncyCastleProvider());

        byte[] data2crypt;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            try(DataOutputStream dos = new DataOutputStream(baos)){
                dos.writeInt(idFacture);
                dos.writeUTF(nomCarte);
                dos.writeUTF(numeroCarte);
            }
            data2crypt = baos.toByteArray();
        }
        dataPay = Crypt.CryptSymAES(sessionkey,data2crypt);
    }

    public byte[] getCryptedData() {
        return dataPay;
    }

    public int getIdFacture(SecretKey sessionkey) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {

        byte[] data = Crypt.DecryptSymAES(sessionkey,dataPay);

        try(ByteArrayInputStream baos = new ByteArrayInputStream(data)){
            try(DataInputStream dos = new DataInputStream(baos)){
                return dos.readInt();
            }
        }
    }

    public String getNomCarte(SecretKey sessionkey) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        byte[] data = Crypt.DecryptSymAES(sessionkey,dataPay);

        try(ByteArrayInputStream baos = new ByteArrayInputStream(data)){
            try(DataInputStream dos = new DataInputStream(baos)){
                dos.readInt();
                return dos.readUTF();
            }
        }
    }

    public String getNumeroCarte(SecretKey sessionkey) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException, IOException {
        byte[] data = Crypt.DecryptSymAES(sessionkey,dataPay);

        try(ByteArrayInputStream baos = new ByteArrayInputStream(data)){
            try(DataInputStream dos = new DataInputStream(baos)){
                dos.readInt();
                dos.readUTF();
                return dos.readUTF();
            }
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidKeyException {
        SecretKey session = SessionKeyGenerator.generateKey("AES");
        SecretKey session2 = SessionKeyGenerator.generateKey("AES");

        sRequetePayFacture pay = new sRequetePayFacture(1,"mael","0123456789",session);

        System.out.println("Affichage cryptée -> " + Arrays.toString(pay.getCryptedData()));
        System.out.println("Affichage des valeurs decryptées ->");
        System.out.println("IFacture -> " + pay.getIdFacture(session));
        System.out.println("Nom -> " + pay.getNomCarte(session));
        System.out.println("Numero -> " + pay.getNumeroCarte(session));
    }
}
