package classes;

import classes.UI.ClientWindow;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientJava {
    public static void main(String... args){
        try {
            Socket csocket = new Socket(args[1],Integer.parseInt(args[2]));

            ClientWindow clientWindow = new ClientWindow(csocket);
            clientWindow.setVisible(true);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
