package com.mycompany.lab3bankingexample.programManagers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_db?createDatabaseIfNotExist=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root"; 
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        return connection;
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create customers table - MYSQL SYNTAX
            String customersTable = "CREATE TABLE IF NOT EXISTS customers ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(255) NOT NULL UNIQUE"
                    + ")";

            // Create accounts table - MYSQL SYNTAX
            String accountsTable = "CREATE TABLE IF NOT EXISTS accounts ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "customer_id INT NOT NULL,"
                    + "account_type VARCHAR(50) NOT NULL,"
                    + "balance DECIMAL(15,2) NOT NULL DEFAULT 0.0,"
                    + "interest_rate DECIMAL(5,2),"
                    + "transaction_count INT DEFAULT 0,"
                    + "FOREIGN KEY (customer_id) REFERENCES customers(id),"
                    + "UNIQUE(customer_id, account_type)"
                    + ")";

            // Create transactions table - MYSQL SYNTAX
            String transactionsTable = "CREATE TABLE IF NOT EXISTS transactions ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "account_id INT NOT NULL,"
                    + "transaction_type VARCHAR(50) NOT NULL,"
                    + "amount DECIMAL(15,2) NOT NULL,"
                    + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "description TEXT,"
                    + "FOREIGN KEY (account_id) REFERENCES accounts(id)"
                    + ")";

            stmt.execute(customersTable);
            stmt.execute(accountsTable);
            stmt.execute(transactionsTable);

            System.out.println("Database tables created successfully!");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void insertSampleData() {
        try (Connection conn = getConnection()) {

            // Check if data already exists
            String checkQuery = "SELECT COUNT(*) FROM customers";
            try (Statement stmt = conn.createStatement();
                 var rs = stmt.executeQuery(checkQuery)) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Sample data already exists. Skipping insertion.");
                    return;
                }
            }

            // Insert customers
            String insertCustomer = "INSERT INTO customers (name) VALUES (?)";

            conn.setAutoCommit(false);

            // Customer 1: John Smith
            try (PreparedStatement pstmt = conn.prepareStatement(insertCustomer, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, "John Smith");
                pstmt.executeUpdate();
                var rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int customerId = rs.getInt(1);
                    insertAccount(conn, customerId, "Saving", 1000.0, 2.5, 0);
                    insertAccount(conn, customerId, "Checking", 1500.0, null, 0);
                }
            }

            // Customer 2: Emma Johnson
            try (PreparedStatement pstmt = conn.prepareStatement(insertCustomer, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, "Emma Johnson");
                pstmt.executeUpdate();
                var rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int customerId = rs.getInt(1);
                    insertAccount(conn, customerId, "Saving", 2500.0, 3.0, 0);
                    insertAccount(conn, customerId, "Checking", 500.0, null, 0);
                }
            }

            // Customer 3: Michael Brown
            try (PreparedStatement pstmt = conn.prepareStatement(insertCustomer, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, "Michael Brown");
                pstmt.executeUpdate();
                var rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int customerId = rs.getInt(1);
                    insertAccount(conn, customerId, "Checking", 3000.0, null, 0);
                }
            }

            // Customer 4: Sophia Davis
            try (PreparedStatement pstmt = conn.prepareStatement(insertCustomer, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, "Sophia Davis");
                pstmt.executeUpdate();
                var rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int customerId = rs.getInt(1);
                    insertAccount(conn, customerId, "Saving", 5000.0, 3.5, 0);
                }
            }

            // Customer 5: William Wilson
            try (PreparedStatement pstmt = conn.prepareStatement(insertCustomer, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, "William Wilson");
                pstmt.executeUpdate();
                var rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int customerId = rs.getInt(1);
                    insertAccount(conn, customerId, "Saving", 1200.0, 2.8, 0);
                    insertAccount(conn, customerId, "Checking", 800.0, null, 0);
                }
            }

            conn.commit();
            System.out.println("Sample data inserted successfully!");

        } catch (SQLException e) {
            System.err.println("Error inserting sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertAccount(Connection conn, int customerId, String accountType, 
                                     Double balance, Double interestRate, int transactionCount) throws SQLException {
        String sql = "INSERT INTO accounts (customer_id, account_type, balance, interest_rate, transaction_count) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setString(2, accountType);
            pstmt.setDouble(3, balance);
            if (interestRate != null) {
                pstmt.setDouble(4, interestRate);
            } else {
                pstmt.setNull(4, java.sql.Types.DECIMAL);
            }
            pstmt.setInt(5, transactionCount);
            pstmt.executeUpdate();
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}