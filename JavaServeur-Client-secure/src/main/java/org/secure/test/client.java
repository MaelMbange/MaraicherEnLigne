package org.secure.test;

import org.bouncycastle.operator.OperatorCreationException;
import org.secure.utilsCrypted.generator.CertificateGenerator;
import org.secure.utilsCrypted.reponses.sReponseLogin;
import org.secure.utilsCrypted.requetes.sRequeteLogin;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class client {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, CertificateException, OperatorCreationException, IOException, ClassNotFoundException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String login = "Mael";
        String password = "halo";

        System.out.println("Initialisation du client ...");
        System.out.println("Creation du certificat ...");
        KeyPair cles = CertificateGenerator.generateKeyPair();
        X509Certificate certificate = CertificateGenerator.generateSelfSignedCertificate(login,cles);
        System.out.println("Certificat crée !");

        System.out.println();
        System.out.println("Creation de la requete ...");
        sRequeteLogin requeteLogin = new sRequeteLogin(login,password,certificate);
        System.out.println("Requete login créée !");

        System.out.println();
        System.out.println("Envoie de la requete au serveur ...");
        Socket socket = new Socket("localhost",10000);

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(requeteLogin);

        System.out.println("Requete envoyée au serveur !");

        System.out.println();
        System.out.println("Attente reponse serveur ...");
        sReponseLogin reponseLogin   ;
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            reponseLogin = (sReponseLogin)ois.readObject();

        System.out.println("Reponse login reçue !");

        System.out.println();
        System.out.println("Recuperation de la cle de session ...");
        SecretKey session = reponseLogin.get_secret_key_decrypted(cles.getPrivate(),"AES");
        System.out.println("Cle de session récupérée !");

        System.out.println();
        System.out.println("Affichage de la reponse -> ");
        System.out.println("Operation reussie -> " + reponseLogin.getSucced(session));
        System.out.println("IdClient -> " + reponseLogin.getIdClient(session));
        System.out.println("cleSession -> " + reponseLogin.get_secret_key_decrypted(cles.getPrivate(),"AES"));

        System.out.println();
        System.out.println("Fin du client !");

    }
}
