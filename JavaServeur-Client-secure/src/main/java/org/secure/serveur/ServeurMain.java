package org.secure.serveur;

import org.secure.utils.interfaces.Logs;
import org.secure.utils.interfaces.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class ServeurMain extends Thread{

    private ServerSocket serverSocket;
    private Protocol protocol;
    private Logs logger;
    private int port;

    private ThreadGroup cGroup;

    public ServeurMain(int port, Protocol protocol, Logs logger) throws IOException {
        this.logger = logger;
        this.protocol = protocol;
        this.port = port;
        this.cGroup = new ThreadGroup("groupeClient");

        serverSocket = new ServerSocket(this.port);
    }

    @Override
    public void run(){
        this.logger.writeLog("Demarrage du serveur!");

        while(!this.isInterrupted()){
            Socket csocket;
            try {
                System.out.println("Thread-Server [CONNECTION= PENDING]");
                serverSocket.setSoTimeout(1000);

                try {
                    csocket = serverSocket.accept();
                } catch (SocketTimeoutException ste) {
                    // Timeout occurred, check if the thread is interrupted
                    continue;
                }

                Thread sThread = new Thread(cGroup,new ThreadServiceClient(protocol,csocket,logger));
                sThread.start();

            } catch (IOException e) {
                System.out.println("Thread-Server [ERROR= while accepting a connection]");
            } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.cGroup.interrupt();
        this.logger.writeLog("Arret du serveur!");
    }
}