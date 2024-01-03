package org.secure.serveur;

import org.secure.utils.exceptions.EOCException;
import org.secure.utils.interfaces.Logs;
import org.secure.utils.interfaces.Protocol;
import org.secure.utils.interfaces.Reponse;
import org.secure.utils.interfaces.Requete;
import org.secure.utils.reponses.ReponseErreur;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ThreadServiceClient extends Thread {
    Protocol protocol;
    Socket csocket;
    Logs logger;

    public ThreadServiceClient(Protocol protocol,Socket socket,Logs logger){
        this.protocol = protocol;
        this.logger = logger;
        this.csocket = socket;
    }

    @Override
    public void run(){
        ObjectInputStream ios = null;
        ObjectOutputStream oos = null;

        try{
            ios = new ObjectInputStream(csocket.getInputStream());
            oos = new ObjectOutputStream(csocket.getOutputStream());

            this.logger.writeLog("Lancement du thread secondaire: " + this.threadId());
            this.logger.writeLog("Hote distant connecté: [HOST=" + csocket.getInetAddress() + ":" + csocket.getPort() + "]");
            while(!isInterrupted()){
                try{

                    Requete requete = (Requete)ios.readObject();
                    Reponse reponse = protocol.analyse(requete);
                    oos.writeObject(reponse);

                } catch (ClassNotFoundException e) {
                    this.logger.writeLog("ThreadSecondaire: " + this.threadId() + " - Envoie : Requete invalide");
                    oos.writeObject(new ReponseErreur("Requete invalide!"));
                }catch (EOCException e) {
                    if(e.getReponse() != null)
                        oos.writeObject(e.getReponse());
                    break;
                }
            }
        } catch(EOFException e){
            this.logger.writeLog("Hote distant déconnecté: [HOST=" + csocket.getInetAddress() + ":" + csocket.getPort() + "]");
        }catch (IOException e) {
            this.logger.writeLog("Erreur thread secondaire: " + this.threadId() + " - " + e.getMessage());
        }
        finally {
            try {
                this.csocket.close();
            } catch (IOException e) {
                this.logger.writeLog("Erreur thread secondaire: " + this.threadId() + " - fermeture de la socket");
            }
            this.logger.writeLog("Fin du thread secondaire: " + this.threadId());
        }
    }
}
