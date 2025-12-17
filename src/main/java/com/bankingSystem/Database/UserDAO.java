// src/main/java/com/bankingSystem/Database/UserDAO.java
package com.bankingSystem.Database;

import com.bankingSystem.user.*;

import java.sql.*;

public class UserDAO {
    private final Connection conn = DatabaseConnection.getInstance().getConnection();

    public void saveUser(User user) {
        String sql = """
MERGE INTO Users KEY(id) VALUES (?, ?, ?, ?)""";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.passwordHash);
            pstmt.setString(4, user.getRole().getDisplayName());
            pstmt.executeUpdate();
            System.out.println("User saved: " + user);
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public User loadUser(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return buildUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User loadUserById(String userId) {
        String sql = "SELECT * FROM Users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return buildUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User buildUserFromResultSet(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String username = rs.getString("username");
        String passwordHash = rs.getString("password");
        String roleStr = rs.getString("role");

        Role role = Role.fromString(roleStr);

        return switch (role) {
            case CUSTOMER -> new Customer(id, username, passwordHash);
            case TELLER   -> new Teller(id, username, passwordHash);
            case MANAGER  -> new Manager(id, username, passwordHash);
            case ADMIN    -> new Admin(id, username, passwordHash);
        };
    }
}