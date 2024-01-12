package org.example.marchand.serveur.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.marchand.serveur.Erreurs.Erreurs;
import org.example.marchand.serveur.Serveur;
import org.example.marchand.utils.Colors.Colors;
import org.example.marchand.utils.data.Article;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public class HandlerAPI implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "ContentType");

        String method = exchange.getRequestMethod();

        if(method.equalsIgnoreCase("GET")) {
            System.out.println(Colors.BrightBlue + "GET ->" + Colors.Default);
            try {
                String reponse = getRowArticle();
                System.out.println(Colors.BrightGreen + "Données à envoyer -> " + getRowArticle() + Colors.Default);

                sendJsonRequest(exchange, 200, reponse);
            } catch (SQLException | IOException e) {
                System.out.println(Colors.BrightRED + e.getMessage() + Colors.Default);
            }
        }
        else if(method.equalsIgnoreCase("POST")) {
            try {
                System.out.println(Colors.BrightBlue + "POST -> " + Colors.Default);
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                System.out.println(Colors.BrightGreen + "body -> " + body + Colors.Default);

                Article article = convertJsonToArticle(body);

                int rows = Serveur.bean.doModifyArticle(article);
                System.out.println(Colors.BrightGreen + "rows affected -> " + rows + Colors.Default);

                if (rows > 0) {
                    sendJsonRequest(exchange, 201, "Le stock à été mis à jour avec succes !");
                } else {
                    sendJsonRequest(exchange, 400, "Échec de la mise à jour du stock.");
                }
            } catch (SQLException | IOException e) {
                System.out.println(Colors.BrightRED + "POST -> " + e.getMessage() + Colors.Default);
            }
        }
        else{
            try {
                Erreurs.Error404(exchange);
            } catch (IOException e) {
                System.out.println(Colors.BrightRED + e.getMessage() + Colors.Default);
            }
        }
        System.out.println(Colors.BrightBlue + "Fin de la réponse !" + Colors.Default);
    }

    private static String getRowArticle() throws SQLException {

        String reponse = "";
        List<Article> articles = Serveur.bean.doSelectArticles();

        reponse += "[";
        for(int i = 0; i < articles.size(); i++){
            if(i == articles.size()-1) reponse += articles.get(i).getRowJson();
            else reponse += articles.get(i).getRowJson() + ",";
        }
        reponse += "]";

        return reponse;
    }

    private static void sendJsonRequest(HttpExchange exchange, int erreurCode, String reponse) throws IOException {
        byte[] reponseBytes = reponse.getBytes(StandardCharsets.UTF_8);
        //System.out.println(Colors.BrightMangenta + "Données bytes à envoyer -> " + Arrays.toString(reponseBytes) + Colors.Default);

        exchange.getResponseHeaders().set("Content-type","application/json");
        exchange.getResponseHeaders().set("Content-Length", String.valueOf(reponseBytes.length));
        exchange.sendResponseHeaders(erreurCode, reponseBytes.length);

        try(OutputStream os = exchange.getResponseBody()){
            os.write(reponse.getBytes());
        }
    }

    private static Article convertJsonToArticle(String json){
        JSONObject jo = new JSONObject(json);
        return new Article(jo.getInt("id"),
                jo.getFloat("prix"),jo.getInt("quantite"));
    }
}
