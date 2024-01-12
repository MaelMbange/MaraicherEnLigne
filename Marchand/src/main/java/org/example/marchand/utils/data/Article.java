package org.example.marchand.utils.data;

import org.example.marchand.utils.Colors.Colors;
import org.json.JSONObject;

public class Article {
    private int id;
    private String intitule;
    private float prix;
    private int quantite;
    private String image;

    public Article(){
        this.id = 0;
        intitule = "";
        prix = 0;
        quantite = 0;
        image = "";
    }

    public Article(int id, float prix, int stock){
        this.id = id;
        this.intitule = "";
        this.prix = prix;
        quantite = stock;
        image = "";
    }

    public Article(int id, String intitule, float prix, int stock){
        this.id = id;
        this.intitule = intitule;
        this.prix = prix;
        quantite = stock;
        image = "";
    }

    public Article(int id, String intitule, float prix, int quantite, String image){
        this.id = id;
        this.intitule = intitule;
        this.prix = prix;
        this.quantite = quantite;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getIntitule() {
        return intitule;
    }

    public float getPrix() {
        return prix;
    }

    public int getQuantite() {
        return quantite;
    }

    public String getImage() {
        return image;
    }


    @Override
    public String toString() {
        return "{\n"+
                "\t\"id\":\"" + this.id+ "\",\n" +
                "\t\"intitule\":\"" + this.intitule + "\",\n" +
                "\t\"prix\":\"" + this.prix + "\",\n" +
                "\t\"quantite\":\"" + this.quantite + "\",\n" +
                "\t\"image\":\"" + this.image + "\"\n" +
                "}";
    }

    public String getJson(int padding){
        String pad = "";
        for(int i = 0; i < padding; i++){
            pad += "\t";
        }

        return  pad + "{\n"+
                pad + "\t\"id\":\"" + this.id+ "\",\n" +
                pad + "\t\"intitule\":\"" + this.intitule + "\",\n" +
                pad + "\t\"prix\":\"" + this.prix + "\",\n" +
                pad + "\t\"quantite\":\"" + this.quantite + "\",\n" +
                pad + "\t\"image\":\"" + this.image + "\"\n" +
                pad + "}";
    }

    public String getRowJson(){
        return "{\"id\":\"" + this.id+ "\"," +
                "\"intitule\":\"" + this.intitule + "\"," +
                "\"prix\":\"" + this.prix + "\"," +
                "\"quantite\":\"" + this.quantite + "\"," +
                "\"image\":\"" + this.image + "\"" + "}";
    }
}
