package RTI.Client;


public class Main {
    public static void main(String[] args){
        UIClient uiClient = new UIClient("localhost",50000,65000);
        uiClient.setVisible(true);
        uiClient.setSize(800,500);
    }
}
