package RTI.PROJET.serverBase.base.SecondaryServerThread;

import RTI.PROJET.serverBase.utils.*;

import java.io.IOException;

public class OnPoolSecondaryServerThread extends AbstractSecondaryServerThread {
    private WaitingList waitingList;

    public OnPoolSecondaryServerThread(Protocol protocol, ThreadGroup group, WaitingList waitinglist, Logs logs) {
        super(protocol, group, logs);
        this.waitingList = waitinglist;
    }

    @Override
    public void run() {
        logs.writeLog("Thread-Server.Client-" + getNumber() + " [STATUS= STARTED]");
        boolean isInterrupted = false;
        while(!isInterrupted){
            try{
                logs.writeLog("Thread-Server.Client-" + getNumber() + " [CONNECTION= WAITING]");
                csocket = waitingList.getConnection();
                logs.writeLog("Thread-Server.Client-" + getNumber() + " [CONNECTION= RECOVER, HOST="+ csocket.getInetAddress()+ ":" + csocket.getPort() +"]");
                super.run();
            }
            catch (InterruptedException e) {
                logs.writeLog("Thread-Server.Client-" + getNumber() + " [STATUS= INTERRUPTED]");
                isInterrupted = true;
            }
        }
        logs.writeLog("Thread-Server.Client-" + getNumber() + " [STATUS= STOPPED]");
    }
}
