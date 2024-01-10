package org.secure.utilsCrypted.reponses;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.secure.utils.interfaces.Reponse;
import org.secure.utilsCrypted.generator.Crypt;
import org.secure.utilsCrypted.generator.SessionKeyGenerator;
import org.secure.utilsCrypted.requetes.sRequetePayFacture;

import javax.crypto.*;
import java.io.*;
import java.security.*;
import java.util.Arrays;

public class sReponsePayFacture implements Reponse {
    private byte[] succeed;
    private byte[] hmac;

    public sReponsePayFacture(boolean succeed, SecretKey sessionKey) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Security.addProvider(new BouncyCastleProvider());

        byte[] data;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            try(DataOutputStream dos = new DataOutputStream(baos)){
                dos.writeBoolean(succeed);
            }
            data = baos.toByteArray();
        }
        //cryptage des données succeed avec la cle de session
        this.succeed = Crypt.CryptSymAES(sessionKey,data);

        //mise en place du hmac
        Mac hm = Mac.getInstance("HMAC-MD5","BC");
        hm.init(sessionKey);
        hm.update(data);
        hmac = hm.doFinal();
    }

    public boolean isSucceed(SecretKey sessionKey) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        Security.addProvider(new BouncyCastleProvider());

        byte[] data = Crypt.DecryptSymAES(sessionKey,succeed);
        try(ByteArrayInputStream baos = new ByteArrayInputStream(data)){
            try(DataInputStream dos = new DataInputStream(baos)){
                return dos.readBoolean();
            }
        }
    }

    public boolean VerifyHMAC(SecretKey sessionKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, IOException, BadPaddingException {
        Mac mac = Mac.getInstance("HMAC-MD5","BC");
        mac.init(sessionKey);
        mac.update(Crypt.DecryptSymAES(sessionKey,succeed));
        byte[] mac_local = mac.doFinal();

        //System.out.println("hmac -> " + Arrays.toString(hmac));
        //System.out.println("mac_local -> " + Arrays.toString(mac_local));
        return MessageDigest.isEqual(mac_local,hmac);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidKeyException {
        SecretKey session = SessionKeyGenerator.generateKey("AES");

        sReponsePayFacture reponse = new sReponsePayFacture(false,session);

        System.out.println("Verification Hmac   -> " + reponse.VerifyHMAC(session));
        System.out.println("Succeed             -> " + reponse.isSucceed(session));
    }
}
