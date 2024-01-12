package org.example.marchand.utils.bean.test;


import org.example.marchand.utils.Colors.Colors;
import org.example.marchand.utils.bean.BeanJDBC;
import org.example.marchand.utils.data.Article;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TestDatabaseConnection {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        BeanJDBC bean = BeanJDBC.getInstance("jdbc:mysql://10.222.13.175/PourStudent","Student","PassStudent1_");

        List<Article> la = bean.doSelectArticles();

        System.out.println("[");
        for(Article a : la)
            System.out.println(a.getJson(1) + ",");
        System.out.println("]");

        bean.doModifyArticle(new Article(1,2.16f,20));
        System.out.println(Colors.BrightRED + "--------------" + Colors.Default);

        System.out.println("[");
        for(Article a : la)
            System.out.println(a.getJson(1) + ",");
        System.out.println("]");
    }
}
