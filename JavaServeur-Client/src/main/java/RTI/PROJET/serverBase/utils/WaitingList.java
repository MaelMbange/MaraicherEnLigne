package RTI.PROJET.serverBase.utils;


import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class WaitingList {
    private final LinkedList<Socket> waitingList;

    public WaitingList(){
        waitingList = new LinkedList<>();
    }

    public synchronized void addConnection(Socket socket){
            waitingList.addLast(socket);
            notify();
    }

    public synchronized Socket getConnection() throws InterruptedException{
        while(waitingList.isEmpty()) wait();
        return waitingList.removeFirst();
    }

    public synchronized void closeConnections() throws IOException {
        for(Socket s : waitingList)
            if(s != null)
                s.close();
    }
}
