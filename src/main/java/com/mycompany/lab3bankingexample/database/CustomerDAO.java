package com.mycompany.lab3bankingexample.database;

import com.mycompany.lab3bankingexample.programManagers.DatabaseManager;
import com.mycompany.lab3bankingexample.models.CustomerAccount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomerDAO {

    public Map<String, CustomerAccount> loadAllCustomers() {
        Map<String, CustomerAccount> customers = new LinkedHashMap<>();

        String query = "SELECT c.id, c.name, "
                + "a.account_type, a.balance, a.interest_rate, a.transaction_count "
                + "FROM customers c "
                + "LEFT JOIN accounts a ON c.id = a.customer_id "
                + "ORDER BY c.id, a.account_type";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            String currentCustomerName = null;
            double savingBalance = 0;
            double checkingBalance = 0;
            double interestRate = 0;
            int savingTransactionCount = 0;
            int checkingTransactionCount = 0;

            while (rs.next()) {
                String customerName = rs.getString("name");
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                Double intRate = rs.getObject("interest_rate") != null ? rs.getDouble("interest_rate") : null;
                int transCount = rs.getInt("transaction_count");

                // New customer
                if (currentCustomerName == null || !currentCustomerName.equals(customerName)) {
                    // Save previous customer if exists
                    if (currentCustomerName != null) {
                        CustomerAccount customer = new CustomerAccount(currentCustomerName, 
                                savingBalance, checkingBalance, interestRate);
                        
                        // Restore transaction counts
                        if (customer.hasSavingAccount()) {
                            customer.getSavingAccount().setTransactionCount(savingTransactionCount);
                        }
                        if (customer.hasCheckingAccount()) {
                            customer.getCheckingAccount().setTransactionCount(checkingTransactionCount);
                        }
                        
                        customers.put(currentCustomerName, customer);
                    }

                    // Reset for new customer
                    currentCustomerName = customerName;
                    savingBalance = 0;
                    checkingBalance = 0;
                    interestRate = 0;
                    savingTransactionCount = 0;
                    checkingTransactionCount = 0;
                }

                // Populate account data
                if (accountType != null) {
                    if (accountType.equals("Saving")) {
                        savingBalance = balance;
                        interestRate = intRate != null ? intRate : 0;
                        savingTransactionCount = transCount;
                    } else if (accountType.equals("Checking")) {
                        checkingBalance = balance;
                        checkingTransactionCount = transCount;
                    }
                }
            }

            // Save last customer
            if (currentCustomerName != null) {
                CustomerAccount customer = new CustomerAccount(currentCustomerName, 
                        savingBalance, checkingBalance, interestRate);
                
                // Restore transaction counts
                if (customer.hasSavingAccount()) {
                    customer.getSavingAccount().setTransactionCount(savingTransactionCount);
                }
                if (customer.hasCheckingAccount()) {
                    customer.getCheckingAccount().setTransactionCount(checkingTransactionCount);
                }
                
                customers.put(currentCustomerName, customer);
            }

        } catch (SQLException e) {
            System.err.println("Error loading customers: " + e.getMessage());
            e.printStackTrace();
        }

        return customers;
    }

    public void updateAccountBalance(String customerName, String accountType, double newBalance, int transactionCount) {
        String query = "UPDATE accounts SET balance = ?, transaction_count = ? "
                + "WHERE customer_id = (SELECT id FROM customers WHERE name = ?) "
                + "AND account_type = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, newBalance);
            pstmt.setInt(2, transactionCount);
            pstmt.setString(3, customerName);
            pstmt.setString(4, accountType);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Account balance updated in database.");
            }

        } catch (SQLException e) {
            System.err.println("Error updating account balance: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void logTransaction(String customerName, String accountType, String transactionType, 
                               double amount, String description) {
        String query = "INSERT INTO transactions (account_id, transaction_type, amount, description) "
                + "SELECT a.id, ?, ?, ? FROM accounts a "
                + "JOIN customers c ON a.customer_id = c.id "
                + "WHERE c.name = ? AND a.account_type = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, transactionType);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, description);
            pstmt.setString(4, customerName);
            pstmt.setString(5, accountType);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error logging transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }
}