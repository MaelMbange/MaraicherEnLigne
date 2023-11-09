package RTI.Client;

public class Main {
    public static void main(String[] args){
        UIClient uiClient = new UIClient("localhost",3306);
        uiClient.setVisible(true);
        uiClient.setSize(800,500);
    }
}
