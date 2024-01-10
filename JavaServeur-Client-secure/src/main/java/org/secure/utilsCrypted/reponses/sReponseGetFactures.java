package org.secure.utilsCrypted.reponses;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.secure.utils.donnees.Facture;
import org.secure.utils.interfaces.Reponse;
import org.secure.utilsCrypted.generator.Crypt;
import org.secure.utilsCrypted.generator.SessionKeyGenerator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class sReponseGetFactures implements Reponse {
    private byte[] listFacture;

    public sReponseGetFactures(List<Facture> factureList, SecretKey sessionKey) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        Security.addProvider(new BouncyCastleProvider());

        byte[] data;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            try(ObjectOutputStream dos = new ObjectOutputStream(baos)){
                dos.writeObject(factureList);
            }
            data = baos.toByteArray();
        }
        listFacture = Crypt.CryptSymAES(sessionKey,data);
    }

    public List<Facture> getFactureList(SecretKey sessionKey) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException, ClassNotFoundException {
        Security.addProvider(new BouncyCastleProvider());

        byte[] data = Crypt.DecryptSymAES(sessionKey,listFacture);
        try(ByteArrayInputStream baos = new ByteArrayInputStream(data)){
            try(ObjectInputStream dos = new ObjectInputStream(baos)){
                return (List<Facture>) dos.readObject();
            }
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidKeyException, ClassNotFoundException {
        SecretKey session = SessionKeyGenerator.generateKey("AES");

        List<Facture> list = new ArrayList<>();
        list.add(new Facture(1, LocalDate.now(),10,1));
        list.add(new Facture(2, LocalDate.now(),1,1));
        list.add(new Facture(3, LocalDate.now(),20,1));
        list.add(new Facture(4, LocalDate.now(),7.5f,0));

        sReponseGetFactures reponse = new sReponseGetFactures(list,session);

        System.out.println("Reception reponse -> ");
        List<Facture> listReception = reponse.getFactureList(session);

        System.out.println("Liste de factures -> " + listReception);
    }
}
