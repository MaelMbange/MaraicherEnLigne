package RTI.PROJET.serverBase.base.SecondaryServerThread;

import RTI.PROJET.serverBase.utils.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class AbstractSecondaryServerThread extends Thread{
    protected Protocol protocol;
    protected Socket csocket;
    protected Logs logs;
    private int number;

    private static int currentNumber = 1;

    public AbstractSecondaryServerThread(Protocol protocol, Socket socket, Logs logs) {
        super("Thread-Server.Client-" + currentNumber + "[PROTOCOL= " + protocol.getName() + "]");
        this.protocol = protocol;
        this.csocket = socket;
        this.logs = logs;
        this.number = currentNumber++;
    }

    public AbstractSecondaryServerThread(Protocol protocol, ThreadGroup group, Logs logs) {
        super(group,"Thread-" + currentNumber + "[PROTOCOL= " + protocol.getName() + "]");
        this.protocol = protocol;
        this.csocket = null;
        this.logs = logs;
        this.number = currentNumber++;
    }

    public int getNumber(){
        return number;
    }

    @Override
    public void run(){

        ObjectInputStream ois;
        ObjectOutputStream oos = null;

        try{
            ois = new ObjectInputStream(csocket.getInputStream());
            oos = new ObjectOutputStream(csocket.getOutputStream());

            while(true){
                Request request = (Request)ois.readObject();
                Response response = protocol.treatment(request, csocket);
                oos.writeObject(response);
                logs.writeLog("4 -> " + (response.toString()));
            }
        }
        catch (EndConnexionException e) {
            logs.writeLog("End Connexion Exception -> " + e.getMessage());
            if(e.getResponse() != null){
                try {
                    oos.writeObject(e.getResponse());
                } catch (IOException ex) {
                    logs.writeLog("Error append while sending message [MESSAGE= " + e.getResponse() +"]");
                }
            }
        }
        catch (ClassNotFoundException e) {
            logs.writeLog("Invalid request recieve");
        }
        catch (IOException e) {
            logs.writeLog("3 Erreur i/O -> " + e.getMessage());
            logs.writeLog("3 Erreur i/O -> " + e.getCause());
            logs.writeLog("3 Erreur i/O -> " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        finally {
            try{
                csocket.close();
            } catch (IOException e) {
                logs.writeLog("Error append while closing socket");
            }
        }
    }
}
