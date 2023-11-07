package RTI.PROJET.bean;
import java.sql.*;
import java.util.Hashtable;


//DriverManager.getConnection("jdbc:mysql://192.168.1.32/PourStudent","Student","PassStudent1_");
public class DatabaseConnection {

    private Connection connection;

    public static final String MYSQL = "mysql";

    private static Hashtable<String,String> drivers;

    static
    {
        drivers = new Hashtable<>();
        drivers.put(MYSQL,"com.mysql.cj.jdbc.Driver");
    }


    public DatabaseConnection(String type, String server, String dbName, String user, String password) throws Exception {
        Class driver = Class.forName(drivers.get(type));

        String url = null;
        switch(type){
            case MYSQL:
                url = "jdbc:mysql://" + server + "/" + dbName;
                break;
        }
        if(url != null)
            connection = DriverManager.getConnection(url,user,password);
        else throw new Exception("Type dataBase is not recognize!");
    }

    public synchronized ResultSet executeQuery(String sql) throws SQLException
    {
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    public synchronized int executeUpdate(String sql) throws SQLException
    {
        Statement statement = connection.createStatement();
        return statement.executeUpdate(sql);
    }

    public synchronized void close() throws SQLException
    {
        if (connection != null && !connection.isClosed())
            connection.close();
    }
}
