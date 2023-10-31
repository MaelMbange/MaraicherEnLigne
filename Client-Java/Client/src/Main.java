import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        Socket csocket;
        for(int i = 0; i < 5 ; i++)
        {
            try
            {
                csocket = new Socket("192.168.0.94",50000);

                System.out.println("--- socket ---");
                System.out.println("Adresse locale : " + csocket.getLocalAddress().getHostAddress());
                System.out.println("port local : " + csocket.getLocalPort());
                System.out.println("Adresse distante : " + csocket.getInetAddress().getHostAddress());
                System.out.println("port distante : " + csocket.getPort());

                csocket.close();

                Thread.sleep(1000);
            }
            catch (IOException e)
            {
                System.out.println("Connexion impossible!");
            } catch (InterruptedException e) {
                System.out.println("Erreur sleep!");
            }
        }
    }
}