package classes.Utile;

public class Article {
    public int Id;
    public String Intitule;
    public float Prix;
    public int Stock;
    public String Image;

    public Article()
    {
        Id = 0;
        Intitule = "";
        Prix = 0;
        Stock = 0;
        Image = "";
    }

    public Article(int id, String intitule, float prix, int stock, String image)
    {
        Id = id;
        Intitule = intitule;
        Prix = prix;
        Stock = stock;
        Image = image;
    }

}
