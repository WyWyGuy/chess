package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword)) {
            try (var stmt = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS " + databaseName)) {
                stmt.executeUpdate();
            }
            try (var stmt = conn.prepareStatement("USE " + databaseName)) {
                stmt.executeUpdate();
            }
            try (var stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "username VARCHAR(128) NOT NULL UNIQUE, " +
                    "password VARCHAR(128) NOT NULL, " +
                    "email VARCHAR(128) NOT NULL)")) {
                stmt.executeUpdate();
            }
            try (var stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS auths (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "authToken VARCHAR(128) NOT NULL, " +
                    "username VARCHAR(128) NOT NULL," +
                    "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE)")) {
                stmt.executeUpdate();
            }
            try (var stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS games (" +
                    "gameID INT PRIMARY KEY AUTO_INCREMENT, " +
                    "whiteUsername VARCHAR(128), " +
                    "blackUsername VARCHAR(128), " +
                    "gameName VARCHAR(128) NOT NULL, " +
                    "game JSON NOT NULL," +
                    "FOREIGN KEY (whiteUsername) REFERENCES users(username) ON DELETE SET NULL," +
                    "FOREIGN KEY (blackUsername) REFERENCES users(username) ON DELETE SET NULL)")) {
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }
}
