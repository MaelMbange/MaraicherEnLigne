package RTI.PROJET.bean.test;

import RTI.PROJET.bean.JavaServerDAL;
import RTI.PROJET.requetesSQL.RequeteSQLGetLogin;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TestDuDAL {
    public static void main(String[] args) {
        try {
            JavaServerDAL jsd = JavaServerDAL.getFactory("192.168.0.202");

            ResultSet res = jsd.GET(new RequeteSQLGetLogin("clients","LeH","1234"));
            boolean hasContent = false;
            while(res.next()){
                System.out.println("login:" + res.getString("login") + " | mdp:" + res.getString("password"));
                hasContent = true;
            }
            if(!hasContent)
                System.out.println("Aucun element retourné");

            jsd.close();
        }
        catch (SQLException e) {
            System.out.println("sqlerror: " + e.getMessage());
        }
        catch (Exception e){
            System.out.println("error: " + e.getMessage());
        }
    }
}
