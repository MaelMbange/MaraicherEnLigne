package org.secure.utils.bean;


import org.secure.utils.requetesSQL.RequeteSQL;

import java.sql.ResultSet;
import java.sql.SQLException;

//data access layer generic
public abstract class GenericDAL {

    final protected DatabaseConnection databaseConnection;

    public GenericDAL(String type, String server, String dbName, String user, String password) throws Exception {
        databaseConnection = new DatabaseConnection(type,server,dbName,user,password);
    }

    protected abstract ResultSet GET(RequeteSQL o) throws SQLException;

    protected abstract int POSTUPDATE(RequeteSQL o) throws SQLException;

    public void close() throws SQLException {
        databaseConnection.close();
    }
}
