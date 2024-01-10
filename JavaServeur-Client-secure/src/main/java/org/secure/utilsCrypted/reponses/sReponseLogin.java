package org.secure.utilsCrypted.reponses;

import org.secure.utils.interfaces.Reponse;
import org.secure.utilsCrypted.generator.Crypt;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;

public class sReponseLogin implements Reponse {
    byte[] dataCrypted;
    byte[] cryptedSessionKey;

    public byte[] getDataCrypted() {
        return dataCrypted;
    }

    public byte[] getCryptedSessionKey() {
        return cryptedSessionKey;
    }

    public sReponseLogin(boolean succeed, int idClient, SecretKey sessionKey, PublicKey publicKey) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] data;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            try(DataOutputStream dos = new DataOutputStream(baos)){
                dos.writeBoolean(succeed);
                dos.writeInt(idClient);
            }
            data = baos.toByteArray();
        }
        //Encryption of the session key
        cryptedSessionKey = Crypt.CryptAsymRSA(publicKey,sessionKey.getEncoded());

        //Encryption of the date using the sessionKey
        dataCrypted = Crypt.CryptSymAES(sessionKey,data);
    }

    public SecretKey get_secret_key_decrypted(PrivateKey privateKey, String algorithm) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        byte[] cle = Crypt.DecryptAsymRSA(privateKey,cryptedSessionKey);
        return new SecretKeySpec(cle,algorithm);
    }

    public byte[] get_data_decrypted(SecretKey cleSession) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        return Crypt.DecryptSymAES(cleSession,dataCrypted);
    }

    public boolean getSucced(SecretKey cleSession) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException, IOException {
        byte[] data = Crypt.DecryptSymAES(cleSession,dataCrypted);

        try(ByteArrayInputStream bais = new ByteArrayInputStream(data)){
            try(DataInputStream dis = new DataInputStream(bais)){
                return dis.readBoolean();
            }
        }
    }

    public int getIdClient(SecretKey cleSession) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException, IOException {
        byte[] data = Crypt.DecryptSymAES(cleSession,dataCrypted);

        try(ByteArrayInputStream bais = new ByteArrayInputStream(data)){
            try(DataInputStream dis = new DataInputStream(bais)){
                dis.readBoolean();
                return dis.readInt();
            }
        }
    }

}
