package test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class serveur {

    public static void test1() throws IOException {
        ServerSocket ssocket;
        Socket csocket;

        ssocket = new ServerSocket(5000);

        csocket = ssocket.accept();

        System.out.println("--- socket ---");
        System.out.println("Adresse locale : " + csocket.getLocalAddress().getHostAddress());
        System.out.println("port local : " + csocket.getLocalPort());
        System.out.println("Adresse client : " + csocket.getInetAddress().getHostAddress());
        System.out.println("port client : " + csocket.getPort());

        DataInputStream dis = new DataInputStream(csocket.getInputStream());
        DataOutputStream dos = new DataOutputStream(csocket.getOutputStream());

        String message = dis.readUTF();
        System.out.println("Message: " + message);

        dos.writeUTF("<serveur> Well recieve");

        csocket.close();
    }

    public static void main(String... agrs) throws IOException {
        //test1();


    }
}
