package org.secure.utilsCrypted.generator;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import java.security.*;

public class Crypt {
    public static byte[] CryptSymDES(SecretKey cle, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        Security.addProvider(new BouncyCastleProvider());
        Cipher chiffrementE = Cipher.getInstance("DES/ECB/PKCS5Padding","BC");
        chiffrementE.init(Cipher.ENCRYPT_MODE, cle);
        return chiffrementE.doFinal(data);
    }

    public static byte[] DecryptSymDES(SecretKey cle,byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Security.addProvider(new BouncyCastleProvider());
        Cipher chiffrementD = Cipher.getInstance("DES/ECB/PKCS5Padding","BC");
        chiffrementD.init(Cipher.DECRYPT_MODE, cle);
        return chiffrementD.doFinal(data);
    }

    public static byte[] CryptSymAES(SecretKey cle, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        Security.addProvider(new BouncyCastleProvider());
        Cipher chiffrementE = Cipher.getInstance("AES/ECB/PKCS5Padding","BC");
        chiffrementE.init(Cipher.ENCRYPT_MODE, cle);
        return chiffrementE.doFinal(data);
    }

    public static byte[] DecryptSymAES(SecretKey cle,byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Security.addProvider(new BouncyCastleProvider());
        Cipher chiffrementD = Cipher.getInstance("AES/ECB/PKCS5Padding","BC");
        chiffrementD.init(Cipher.DECRYPT_MODE, cle);
        return chiffrementD.doFinal(data);
    }

    public static byte[] CryptAsymRSA(PublicKey cle, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Security.addProvider(new BouncyCastleProvider());
        Cipher chiffement = Cipher.getInstance("RSA/ECB/PKCS1Padding","BC");
        chiffement.init(Cipher.ENCRYPT_MODE,cle);
        return chiffement.doFinal(data);
    }

    public static byte[] DecryptAsymRSA(PrivateKey cle, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Security.addProvider(new BouncyCastleProvider());
        Cipher chiffement = Cipher.getInstance("RSA/ECB/PKCS1Padding","BC");
        chiffement.init(Cipher.DECRYPT_MODE,cle);
        return chiffement.doFinal(data);
    }
}