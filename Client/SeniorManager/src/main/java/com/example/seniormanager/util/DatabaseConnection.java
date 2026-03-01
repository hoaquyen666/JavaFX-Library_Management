package com.example.seniormanager.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static String IP;
    private static String Port;
    private static String DB;

    private static String User;
    private static String Pass;
    private static String URL;


    private static String DBP = "db.properties";
    private static String Security= "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    static
    {
        try
        {
            Properties props = new Properties();
            InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(DBP);
            props.load(input);

            IP = props.getProperty("db.ip");
            Port = props.getProperty("db.port");
            DB = props.getProperty("db.name");

            User = props.getProperty("db.user");
            Pass = props.getProperty("db.password");

            URL = "jdbc:mysql://" + IP + ":" + Port + "/" + DB + Security;
        }
        catch (Exception e)
        {
            MessageBox.showError("Lỗi kết nốt [1]", e.getMessage() != null
                    ? e.getMessage() : "Lỗi không xác định [1]");
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        try {
            return DriverManager.getConnection(URL, User, Pass);
        }
        catch (SQLException e)
        {
            MessageBox.showError("Lỗi kết nốt [1.1]", e.getMessage() != null
                    ? e.getMessage() : "Lỗi không xác định [1.1]");

            e.printStackTrace();
            return null;
        }
    }
}
