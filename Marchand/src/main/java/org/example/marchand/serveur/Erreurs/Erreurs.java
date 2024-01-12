package org.example.marchand.serveur.Erreurs;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class Erreurs {
    public static void Error404(HttpExchange exchange) throws IOException {
        String badRequest = "{\"statusCode\":404,\"error\":\"Not Found\",\"message\":\"Not Found\"}";
        exchange.getResponseHeaders().set("Content-type","application/json");
        exchange.sendResponseHeaders(404,badRequest.length());
        try(OutputStream os = exchange.getResponseBody()){
            os.write(badRequest.getBytes());
        }
    }
}
