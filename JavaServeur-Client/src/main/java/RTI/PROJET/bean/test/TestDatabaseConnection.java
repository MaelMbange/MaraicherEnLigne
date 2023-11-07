package RTI.PROJET.bean.test;

import RTI.PROJET.bean.DatabaseConnection;

import java.sql.ResultSet;

public class TestDatabaseConnection {
    public static void main(String[] args){
        try{
            DatabaseConnection connectionDB = new DatabaseConnection(DatabaseConnection.MYSQL,"192.168.1.32","PourStudent","Student","PassStudent1_");

            String requete = "select * from articles";
            ResultSet resultat = connectionDB.executeQuery(requete);

            while(resultat.next()){
                System.out.println("intitule:" + resultat.getString("intitule"));
            }

            resultat.close();
            connectionDB.close();

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
