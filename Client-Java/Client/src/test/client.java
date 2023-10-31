package test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class client {

    public static void test1() throws IOException{
        Socket csocket = new Socket("localhost",5000);

        System.out.println("--- socket ---");
        System.out.println("Adresse locale : " + csocket.getLocalAddress().getHostAddress());
        System.out.println("port local : " + csocket.getLocalPort());
        System.out.println("Adresse serveur : " + csocket.getInetAddress().getHostAddress());
        System.out.println("port serveur : " + csocket.getPort());

        DataOutputStream dos = new DataOutputStream(csocket.getOutputStream());
        DataInputStream dis = new DataInputStream(csocket.getInputStream());

        dos.writeUTF("<client> Hello world");
        String reponse = dis.readUTF();

        System.out.println("Reponse: " + reponse);

        csocket.close();
    }

    public static void main(String... args) throws IOException {
        //test1();

    }
}
