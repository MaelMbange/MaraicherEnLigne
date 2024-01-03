package org.secure.utils.bean;


import org.secure.utils.requetesSQL.RequeteSQL;

import java.sql.ResultSet;
import java.sql.SQLException;

//classe bean metier
//data access layer generic
public class JavaServerDAL extends GenericDAL{
    private static JavaServerDAL jsd = null;

    private JavaServerDAL(String type, String server, String dbName, String user, String password) throws Exception {
        super(type, server, dbName, user, password);
    }

    public static JavaServerDAL getFactory(String server) throws Exception {
        if(jsd == null){
            jsd = new JavaServerDAL(DatabaseConnection.MYSQL,server,"PourStudent","Student","PassStudent1_");
            return jsd;
        }
        return jsd;
    }

    @Override
    public ResultSet GET(RequeteSQL o) throws SQLException {
        return databaseConnection.executeQuery(o.getRequest());
    }

    @Override
    public int POSTUPDATE(RequeteSQL o) throws SQLException{
        return databaseConnection.executeUpdate(o.getRequest());
    }
}
