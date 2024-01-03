package org.secure;

import org.secure.Client.UIClient;
import org.secure.serveur.Graphic;

public class Main {
    public static void main(String[] args){
        new Thread(){
            @Override
            public void run(){
                Graphic sg = new Graphic();
                sg.setVisible(true);
            }
        }.start();

        new Thread(){
            @Override
            public void run(){
                UIClient uiClient = new UIClient("localhost",6500);
                uiClient.setSize(800,500);
                uiClient.setVisible(true);
            }
        }.start();

        while(true);
    }
}
