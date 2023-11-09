package RTI.Client;

import RTI.PROJET.requetesNet.NewMessageDataType;
import RTI.PROJET.requetesNet.NewReponse;
import RTI.PROJET.requetesNet.NewRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class mynet {
    public static void EnvoyerGetFactures(Socket csocket, int idClient){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(csocket.getOutputStream());
            oos.writeObject(new NewRequest(NewMessageDataType.GET_FACTURES,String.valueOf(idClient)));
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void EnvoyerLogin(Socket csocket, String username,String password){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(csocket.getOutputStream());
            oos.writeObject(new NewRequest(NewMessageDataType.LOGIN,username+"/"+password));
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void EnvoyerPayerFacture(Socket csocket, int idFacture,String proprietaire,String NumeroCarte){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(csocket.getOutputStream());
            oos.writeObject(new NewRequest(NewMessageDataType.PAY_FACTURE,idFacture+"/"+proprietaire+"/"+NumeroCarte));
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void EnvoyerLogout(Socket csocket){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(csocket.getOutputStream());
            oos.writeObject(new NewRequest(NewMessageDataType.LOGOUT,""));
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static NewReponse RecevoirReponse(Socket csocket){
        try{
            ObjectInputStream ois = new ObjectInputStream(csocket.getInputStream());
            NewReponse rep = (NewReponse)ois.readObject();
            return rep;
        }
        catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
