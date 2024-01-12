package org.example.marchand.utils.bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseBean {

    private Connection connection;

    public DatabaseBean(String url, String login, String password) throws SQLException, ClassNotFoundException {
        //Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(url,login,password);
    }

    public synchronized Connection getConnection() {
        return connection;
    }

    public synchronized void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed())
            connection.close();
    }
}
