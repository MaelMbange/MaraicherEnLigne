package classes.lib;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class mynet {
    public static void EnvoyerData(Socket socket, String message) throws IOException {

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        String formated = message + "\r\n";
        dos.writeBytes(formated);
        System.out.println("Envoi de : " + formated);
    }

    public static String RecevoirData(Socket socket) throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        StringBuilder sb = new StringBuilder();
        byte b;

        while(true){
            b = dis.readByte();
            //System.out.println((char)b);
            sb.append((char)b);
            if (sb.toString().endsWith("\r\n")) {
                System.out.println("Fin du message");
                break;
            }
        }
        String resultat = sb.toString();
        System.out.println("Reception de " + resultat);
        return resultat;
    }
}
