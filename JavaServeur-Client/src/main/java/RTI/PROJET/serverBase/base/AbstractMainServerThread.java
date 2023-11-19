package RTI.PROJET.serverBase.base;

import RTI.PROJET.serverBase.utils.*;

import java.io.IOException;
import java.net.ServerSocket;

public abstract class AbstractMainServerThread extends Thread {
    protected int port;
    protected Protocol protocol;
    protected Logs logs;

    protected ServerSocket serverSocket;

    public AbstractMainServerThread(int port, Protocol protocol, Logs logs) throws IOException{
        super("Thread-Server" + "[PORT=" + port +", PROTOCOL="+ protocol.getName() +"]");
        this.port    = port;
        this.protocol = protocol;
        this.logs = logs;

        serverSocket = new ServerSocket(port);
    }
}
