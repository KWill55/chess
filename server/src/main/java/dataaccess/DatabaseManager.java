package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    static {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            DATABASE_NAME = props.getProperty("db.name");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");

            var host = props.getProperty("db.host");
            var port = Integer.parseInt(props.getProperty("db.port"));
            CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    static {
        try {
            createDatabase();
            initializeTables();
        } catch (DataAccessException e) {
            throw new RuntimeException("Error initializing database: " + e.getMessage());
        }
    }

    public static void createDatabase() throws DataAccessException {
        try {
            String statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
                 PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static void initializeTables() throws DataAccessException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // Users table
            String createUsers = "CREATE TABLE IF NOT EXISTS Users (" +
                    "username VARCHAR(255) PRIMARY KEY," +
                    "password VARCHAR(255) NOT NULL," +
                    "email VARCHAR(255) NOT NULL" +
                    ")";
            stmt.executeUpdate(createUsers);

            // AuthTokens table
            String createAuthTokens = "CREATE TABLE IF NOT EXISTS AuthTokens (" +
                    "authToken VARCHAR(255) PRIMARY KEY," +
                    "username VARCHAR(255) NOT NULL," +
                    "FOREIGN KEY (username) REFERENCES Users(username) ON DELETE CASCADE" +
                    ")";
            stmt.executeUpdate(createAuthTokens);

            // Games table
            String createGames = "CREATE TABLE IF NOT EXISTS Games (" +
                    "gameID INT AUTO_INCREMENT PRIMARY KEY," +
                    "whiteUsername VARCHAR(255)," +
                    "blackUsername VARCHAR(255)," +
                    "gameName VARCHAR(255) NOT NULL," +
                    "gameState TEXT NOT NULL," +
                    "gameOver BOOLEAN NOT NULL DEFAULT FALSE," +
                    "FOREIGN KEY (whiteUsername) REFERENCES Users(username) ON DELETE SET NULL," +
                    "FOREIGN KEY (blackUsername) REFERENCES Users(username) ON DELETE SET NULL" +
                    ")";
            stmt.executeUpdate(createGames);

        } catch (SQLException e) {
            throw new DataAccessException("Error initializing tables: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws DataAccessException {
        try {
            Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
