package RTI.PROJET.serverBase.utils;


import java.net.Socket;

public interface Protocol {
    String getName();

    Response treatment(Request request, Socket socket) throws EndConnexionException;
}
