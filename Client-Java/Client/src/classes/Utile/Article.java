package classes.Utile;

public class Article {
    private int Id;
    private String Intitule;
    private float Prix;
    private int Stock;
    private String Image;

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

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getIntitule() {
        return Intitule;
    }

    public void setIntitule(String intitule) {
        Intitule = intitule;
    }

    public float getPrix() {
        return Prix;
    }

    public void setPrix(float prix) {
        this.Prix = prix;
    }

    public int getStock() {
        return Stock;
    }

    public void setStock(int stock) {
        this.Stock = stock;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        this.Image = image;
    }
}
