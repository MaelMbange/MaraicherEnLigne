package org.secure.Client;


public class Main {
    public static void main(String[] args){
        UIClient uiClient = new UIClient("localhost",6500);
        uiClient.setVisible(true);
        uiClient.setSize(800,500);
    }
}
