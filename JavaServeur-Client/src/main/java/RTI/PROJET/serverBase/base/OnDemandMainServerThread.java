package RTI.PROJET.serverBase.base;


import RTI.PROJET.serverBase.base.SecondaryServerThread.OnDemandSecondaryServerThread;
import RTI.PROJET.serverBase.utils.*;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class OnDemandMainServerThread extends AbstractMainServerThread {

    public OnDemandMainServerThread(int port, Protocol protocol, Logs logs) throws IOException {
        super(port, protocol, logs);
    }

    @Override
    public void run(){
        logs.writeLog("Thread-Server [STATUS= STARTED]");
        while(!this.isInterrupted()){
            Socket csocket;

            try {
                logs.writeLog("Thread-Server [CONNECTION= PENDING]");
                    serverSocket.setSoTimeout(2000);
                    csocket = serverSocket.accept();
                logs.writeLog("Thread-Server [CONNECTION= ESTABLISHED, HOST="+ csocket.getInetAddress()+ ":" + csocket.getPort() +"]");

                Thread threadConnection = new OnDemandSecondaryServerThread(protocol,csocket,logs);
                threadConnection.start();
            }
            catch (SocketException e) {
                //To Enable the isInterruptcondition
            }
            catch (IOException e) {
                System.out.println("Thread-Server [ERROR= while accepting a connection]");
            }
        }
        logs.writeLog("Thread-Server [STATUS= STOPPED]");
        try{
            serverSocket.close();
        }
        catch (IOException e) {
            System.out.println("Error append while closing socket");
        }
    }
}
