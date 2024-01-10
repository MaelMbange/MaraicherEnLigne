package org.secure.test;

import org.secure.utilsCrypted.generator.SessionKeyGenerator;
import org.secure.utilsCrypted.reponses.sReponseLogin;
import org.secure.utilsCrypted.requetes.sRequeteLogin;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class serveur {
    private static Map<String,String> db = new HashMap<>();

    static
    {
        db.put("Mael","Halo");
    }



    public static void main(String[] args) throws IOException, ClassNotFoundException, CertificateEncodingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        X509Certificate certificate;
        SecretKey sessionKey = SessionKeyGenerator.generateKey("AES");
        System.out.println("Clé de session -> \033[92m" + sessionKey + "\033[0m");

        System.out.println("En attente d'une requete ...");
        ServerSocket ss = new ServerSocket(10000);
            Socket client = ss.accept();
            sRequeteLogin requete;

            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
            requete = (sRequeteLogin) ois.readObject();

        ss.close();
        System.out.println("RequeteLogin recue");
        System.out.println("login -> " + requete.getLogin());

        System.out.println();
        System.out.println("Recuperation du certificat ...");
        certificate = requete.getCertificate();
        System.out.println("certificat -> " + requete.getCertificate());


        System.out.println();
        System.out.println("Verification du mot de passe ...");

        sReponseLogin reponseLogin;
        if(requete.VerifyPassword(db.get(requete.getLogin()))){
            System.out.println("Mot de passe correcte !");

            System.out.println();
            reponseLogin = new sReponseLogin(true,1,sessionKey, certificate.getPublicKey());

        }
        else{
            System.out.println("Mot de passe incorrecte !");

            System.out.println();
            reponseLogin = new sReponseLogin(false,0,sessionKey, certificate.getPublicKey());
        }

        System.out.println();
        System.out.println("Envoie de la reponse au client ...");
            try(ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream())){
                oos.writeObject(reponseLogin);
            }
        System.out.println("Reponse Envoyée !");
        client.close();
        //ss.close();
    }
}
