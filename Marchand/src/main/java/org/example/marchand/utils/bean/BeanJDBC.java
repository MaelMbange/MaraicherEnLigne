package org.example.marchand.utils.bean;

import org.example.marchand.utils.data.Article;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BeanJDBC {
    final private DatabaseBean databaseBean;
    static private BeanJDBC beanJDBC = null;

    private BeanJDBC(DatabaseBean databaseBean) {
        this.databaseBean = databaseBean;
    }

    public static BeanJDBC getInstance(String url, String login, String password) throws SQLException, ClassNotFoundException {
        if(beanJDBC != null) return beanJDBC;
        beanJDBC = new BeanJDBC(new DatabaseBean(url,login,password));
        return beanJDBC;
    }

    public synchronized List<Article> doSelectArticles() throws SQLException {
        Connection connection = databaseBean.getConnection();

        List<Article> listArticle = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM articles;")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            //Ajout des tuple à ma liste.
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("intitule");
                float prix = resultSet.getFloat("prix");
                int stock = resultSet.getInt("stock");
                String image = resultSet.getString("image");

                Article article = new Article(id,nom,prix,stock,image);
                listArticle.add(article);
            }
        }

        return listArticle;
    }

    public synchronized int doModifyArticle(Article article) throws SQLException {
        Connection connection = databaseBean.getConnection();

        int r;

        String sql = "UPDATE articles SET prix = ?, stock = ? WHERE id = ?;";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setFloat(1,article.getPrix());
            preparedStatement.setInt(2,article.getQuantite());
            preparedStatement.setInt(3,article.getQuantite());

            //Ne pas mettre de return ici car cela empeche la requete d'etre effectuée !
            r =  preparedStatement.executeUpdate();
        }
        return r;
    }
}
