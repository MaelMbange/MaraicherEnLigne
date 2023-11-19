package RTI.PROJET.serverBase.base;

import RTI.PROJET.serverBase.base.SecondaryServerThread.OnPoolSecondaryServerThread;
import RTI.PROJET.serverBase.utils.*;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class OnPoolMainServerThread extends AbstractMainServerThread {

    private final WaitingList waitingList;
    private final ThreadGroup groupClient;
    private final int poolSize;

    public OnPoolMainServerThread(int port, Protocol protocol, int poolSize, Logs logs) throws IOException {
        super(port, protocol, logs);
        waitingList = new WaitingList();
        this.groupClient = new ThreadGroup("POOL");
        this.poolSize = poolSize;
    }

    @Override
    public void run() {
        logs.writeLog("Thread-Server [STATUS= STARTED]");

        for(int i = 0; i < poolSize; i++){
            new OnPoolSecondaryServerThread(protocol,groupClient,waitingList,logs).start();
        }

        while(!this.isInterrupted()){
            Socket csocket;

            try{
                System.out.println("Thread-Server [CONNECTION= PENDING]");
                    serverSocket.setSoTimeout(2000);
                    csocket = serverSocket.accept();
                    waitingList.addConnection(csocket);
                logs.writeLog("Thread-Server [CONNECTION= ESTABLISHED, HOST="+ csocket.getInetAddress()+ ":" + csocket.getPort() +"]");
            }
            catch (SocketException e) {
                logs.writeLog("1 -> " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Thread-Server [ERROR= while accepting a connection]");
            }
        }
        try{
            waitingList.closeConnections();
            serverSocket.close();
        }
        catch (IOException e) {
            System.out.println("Error append while closing socket");
        }
        logs.writeLog("Thread-Server [STATUS= STOPPED]");
        groupClient.interrupt();
    }
}
