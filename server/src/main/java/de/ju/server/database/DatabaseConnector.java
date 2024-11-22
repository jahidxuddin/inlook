package de.ju.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/email_server_db";
    private static final String USER = "user";
    private static final String PASSWORD = "admin123";

    private static final class InstanceHolder {
        private static final DatabaseConnector INSTANCE = new DatabaseConnector();
    }

    public static DatabaseConnector getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
