package HealthCentreMemberSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=HealthCentreDB;encrypt=true;trustServerCertificate=true;";
    // Credentials for SQL Authentication
    private static final String USER = "sa";
    private static final String PASSWORD = "password123"; 

    /**
     * Establishes and returns a connection to the database.
     * @return 
     * @throws java.sql.SQLException
     */
    public static Connection getConnection() throws SQLException {
        // Use the DriverManager method that accepts URL, Username, and Password
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}