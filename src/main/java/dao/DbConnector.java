package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbConnector {
    private static String dbPath = "jdbc:h2:tcp://localhost/~/madb";
    private static String name = "sa";
    private static String password = "";

    public static Connection connect() {

        Connection connect = null;
        try {
            Class.forName("org.h2.Driver");
            connect = DriverManager.getConnection(dbPath, name, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connect;
    }
}
