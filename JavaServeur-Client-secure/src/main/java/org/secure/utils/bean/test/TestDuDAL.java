package org.secure.utils.bean.test;


import org.secure.utils.bean.DatabaseConnection;
import org.secure.utils.bean.JavaServerDAL;
import org.secure.utils.requetesSQL.RequeteSQLGetLogin;
import org.secure.utils.requetesSQL.RequeteSQLGetLoginv2;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TestDuDAL {
    public static void main(String[] args) {
        try {
            JavaServerDAL jsd = JavaServerDAL.getFactory("192.168.1.50");

            ResultSet res = jsd.GET(new RequeteSQLGetLoginv2("Wagner"));
            boolean hasContent = false;
            while(res.next()){
                //System.out.println("Exists : " + String.valueOf(res.getBoolean(1)).toUpperCase() + " | ID: " + res.getInt(2));
                System.out.println("exist? -> " + res.getBoolean(1));
                System.out.println("id -> " + res.getInt(2));
                System.out.println("password: " + res.getString(3));
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
