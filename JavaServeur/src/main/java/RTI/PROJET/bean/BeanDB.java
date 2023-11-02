package RTI.PROJET.bean;
import java.sql.*;

public class BeanDB {

    public static void main(String[] args) throws ClassNotFoundException {
        try
        {
            Class driver = Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Obtention du driver OK...");

            Connection con = DriverManager.getConnection("jdbc:mysql://192.168.228.167/PourStudent","Student","PassStudent1_");
            System.out.println("Connexion à la BD PourStudent OK...");

            Statement instruction = con.createStatement();
            System.out.println("Creation du Statement OK...");

            ResultSet rs = instruction.executeQuery("SELECT * FROM Personnes");
            System.out.println("ExecuteQuery OK...");
            System.out.println("Instruction SELECT * FROM Personnes");

            System.out.println("Nombre de colonnes = " + rs.getMetaData().getColumnCount());

            for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++)
                System.out.print(rs.getMetaData().getColumnName(j) + "\t");
            System.out.println();

            while (rs.next())
            {
                for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++)
                    System.out.print(rs.getObject(j) + "\t");
                System.out.println();
            }
            System.out.println();

            /*Statement instruc = con.createStatement();
            boolean retour = instruc.execute("select count(*) from Personnes");
            System.out.println("Instruction SELECT COUNT(*) sur Personnes");
            System.out.println("retour = " + retour);
            ResultSet rsc = instruc.getResultSet();
            if (rsc==null) System.out.println("Pas de resultset");
            else
            {
                System.out.println("Resultset récupéré");
                if (rsc.next())
                {
                    short nbre = rsc.getShort(1);
                    System.out.println("Nombre de tuples = " + nbre);
                }
            }
            // Fermeture de la connexion
            rsc.close();*/
            rs.close();
            con.close();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
