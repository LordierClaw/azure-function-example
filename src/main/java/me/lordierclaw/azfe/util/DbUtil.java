package me.lordierclaw.azfe.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {
    public static Connection getConnection() throws SQLException {
        // Register the JDBC driver
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String connectionUrl = System.getenv("JDBC_CONNECTION_URL");
        System.out.println(connectionUrl);
        return DriverManager.getConnection(connectionUrl);
    }
}
