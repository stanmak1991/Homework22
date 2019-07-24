package dao;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbConnector {
    private static final Logger LOGGER = Logger.getLogger(DbConnector.class);
    private static String dbPath = "jdbc:h2:tcp://localhost/~/test";
    private static String name = "sa";
    private static String password = "";

    public static Connection connect() {

        Connection connect = null;
        try {
            Class.forName("org.h2.Driver");
            connect = DriverManager.getConnection(dbPath, name, password);
        } catch (Exception e) {
            LOGGER.error("Can not connect to database " + dbPath, e);
        }
        return connect;
    }
}
