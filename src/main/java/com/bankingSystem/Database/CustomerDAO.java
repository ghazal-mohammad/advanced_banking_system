package com.bankingSystem.Database;

// src/main/java/com/bankingSystem/Database/CustomerDAO.java

import com.bankingSystem.Roles.Customer;
import java.sql.*;

public class CustomerDAO {
    private final Connection conn = DatabaseConnection.getInstance().getConnection();

    public void save(Customer customer) {
        String sql = "INSERT OR REPLACE INTO Users (id, username, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getFullName().replace(" ", "_").toLowerCase());
            pstmt.setString(3, "default_pass"); // أو UUID.randomUUID()
            pstmt.setString(4, "Customer");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving customer: " + e.getMessage());
        }
    }
}