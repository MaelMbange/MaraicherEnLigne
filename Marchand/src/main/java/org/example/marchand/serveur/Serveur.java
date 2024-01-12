package org.example.marchand.serveur;

import com.sun.net.httpserver.HttpServer;
import org.example.marchand.serveur.handlers.*;
import org.example.marchand.utils.Colors.Colors;
import org.example.marchand.utils.bean.BeanJDBC;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

public class Serveur {
    public static final BeanJDBC bean;

    static {
        try {
            bean = BeanJDBC.getInstance("jdbc:mysql://10.222.13.175/PourStudent","Student","PassStudent1_");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080),0);
            server.createContext("/api",new HandlerAPI());
            server.createContext("/",new HandlerHtml());
            server.createContext("/css",new HandlerCSS());
            server.createContext("/images",new HandlerImages());
            server.createContext("/scripts",new HandlerScript());
            server.start();
            System.out.println(Colors.BrightGreen + "Demarage du serveur !" + Colors.Default);

        } catch (
                IOException e) {
            System.out.println(Colors.BrightRED + "Erreur serveur ! " + Colors.Default);
        }
    }
}
