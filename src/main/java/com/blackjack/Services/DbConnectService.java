package com.blackjack.Services;

import java.sql.*;

// Connecting to the Database and Initializing the tables needed.
public class DbConnectService {

    // The connection details are read from environment variables, so no
    // credentials are stored in the source code. Set these three before running:
    //   BLACKJACK_DB_URL       e.g. jdbc:postgresql://<host>:<port>/postgres
    //   BLACKJACK_DB_USER      the database username
    //   BLACKJACK_DB_PASSWORD  the database password
    private static final String URL_VAR = "BLACKJACK_DB_URL";
    private static final String USER_VAR = "BLACKJACK_DB_USER";
    private static final String PASSWORD_VAR = "BLACKJACK_DB_PASSWORD";

    public static void main(String[] args) {
        try (Connection connection = connect()) {
            System.out.println("Connected to Supabase PostgreSQL database!");
            // Step 1: Create Tables
            createTables(connection);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createTables(Connection connection) {
        String query = "CREATE TABLE IF NOT EXISTS Users (" +
                "id SERIAL PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "chips BIGINT DEFAULT 0, " +  // Chip balance
                "wins INT DEFAULT 0, " +      // Number of wins
                "losses INT DEFAULT 0, " +    // Number of losses
                "pushes INT DEFAULT 0)";      // Number of pushes (ties)

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(query);
            System.out.println("Users table created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to get a database connection
    public static Connection connect() throws SQLException {
        String url = requireEnv(URL_VAR);
        String user = requireEnv(USER_VAR);
        String password = requireEnv(PASSWORD_VAR);
        return DriverManager.getConnection(url, user, password);
    }

    // Reads one environment variable. A missing variable is reported as an
    // SQLException because every caller of connect() already handles that type,
    // so a missing setting is treated the same way as a failed connection.
    private static String requireEnv(String name) throws SQLException {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new SQLException("Database is not configured: environment variable "
                    + name + " is not set.");
        }
        return value;
    }
}
