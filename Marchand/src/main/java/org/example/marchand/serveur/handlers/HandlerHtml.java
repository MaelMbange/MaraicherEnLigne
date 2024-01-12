package org.example.marchand.serveur.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.marchand.serveur.Erreurs.Erreurs;
import org.example.marchand.utils.Colors.Colors;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class HandlerHtml implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "ContentType");

        String method = exchange.getRequestMethod();

        if(method.equalsIgnoreCase("GET")){
            String requestPath = exchange.getRequestURI().getPath();
            System.out.println(Colors.BrightBlue + "[Request URI:" +  requestPath + "]" + Colors.Default);

            File file = new File("html/index.html");
            System.out.println(Colors.BrightGreen + "Html file found: " + file.exists() + Colors.Default);

            if(file.exists()){
                exchange.getResponseHeaders().set("Content-type","text/html");
                exchange.sendResponseHeaders(200, file.length());
                try( OutputStream os = exchange.getResponseBody()){
                    Files.copy(file.toPath(),os);
                }
                System.out.println(Colors.BrightBlue + "Reponse envoyée !" + Colors.Default);
            }
            else
                Erreurs.Error404(exchange);
        }
        else
            Erreurs.Error404(exchange);
    }
}
