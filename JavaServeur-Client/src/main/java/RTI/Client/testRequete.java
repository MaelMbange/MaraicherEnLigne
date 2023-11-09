package RTI.Client;

import RTI.PROJET.requetesNet.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;

public class testRequete {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost",3306);

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());


        oos.writeObject(new NewRequest(NewMessageDataType.GET_FACTURES,"2"));
        NewReponse rl =  (NewReponse)ois.readObject();
        if(rl != null){
            System.out.println(rl.getContent());

            String[] content = rl.getContent().split("/");
            if(!content[0].equals("false"))
                for(int i = 0; i < content.length-1;i += 4){
                    System.out.println("id:" +Integer.parseInt(content[i]));
                    System.out.println("date:" + LocalDate.parse(content[i+1]));
                    System.out.println("montant:" +Float.parseFloat(content[i+2]));
                    System.out.println("paye?:" + Boolean.parseBoolean(content[i+3]));
                }
            else
                System.out.println("Liste vide");
            socket.close();
        }
        System.out.println("ReadObjet is null");
    }
}
