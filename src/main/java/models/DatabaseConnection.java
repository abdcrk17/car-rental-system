package models;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton class to manage the database connection.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:carrental.db";
    private static boolean isInitialized = false;

    private DatabaseConnection() {}

    /**
     * Gets a new database connection instance.
     * @return A new database connection.
     * @throws SQLException If a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found.");
            e.printStackTrace();
        }
        
        Connection connection = DriverManager.getConnection(URL);
        
        if (!isInitialized) {
            initializeDatabase(connection);
            isInitialized = true;
        }
        
        return connection;
    }

    /**
     * Initializes the database by executing the schema.sql script.
     */
    private static void initializeDatabase(Connection conn) {
        try (InputStream is = DatabaseConnection.class.getResourceAsStream("/schema.sql")) {
            if (is == null) {
                System.err.println("schema.sql not found in resources!");
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sql.append(line);
                if (line.trim().endsWith(";")) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(sql.toString());
                    } catch (SQLException e) {
                        System.err.println("Error executing SQL: " + sql);
                        e.printStackTrace();
                    }
                    sql.setLength(0); // Reset for the next statement
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize database schema.");
            e.printStackTrace();
        }
    }
}
