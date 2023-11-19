package RTI.PROJET.serverBase.base.SecondaryServerThread;

import RTI.PROJET.serverBase.utils.*;

import java.io.IOException;
import java.net.Socket;

public class OnDemandSecondaryServerThread extends AbstractSecondaryServerThread {
    public OnDemandSecondaryServerThread(Protocol protocol, Socket socket, Logs logs) throws IOException {
        super(protocol, socket, logs);
    }

    @Override
    public void run(){
        logs.writeLog("Thread-Server.Client [STATUS= STARTED]");
            super.run();
        logs.writeLog("Thread-Server.Client [STATUS= STOPPED]");
    }
}
