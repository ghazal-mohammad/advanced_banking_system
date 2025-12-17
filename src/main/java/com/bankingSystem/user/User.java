// src/main/java/com/bankingSystem/Roles/User.java
package com.bankingSystem.user;

import com.bankingSystem.Database.UserDAO;

import java.util.UUID;

public abstract class User {
    protected String userId;
    protected String username;
    public String passwordHash; // In real system: use BCrypt
    protected Role role;

    public User(String username, String password, Role role) {
        this.userId = UUID.randomUUID().toString();
        this.username = username;
        this.passwordHash = hashPassword(password); // Simple placeholder
        this.role = role;
    }

    // For loading from DB
    protected User(String userId, String username, String passwordHash, Role role) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Simple placeholder hash (in real project use BCrypt or Argon2)
    private String hashPassword(String password) {
        return password != null ? Integer.toHexString(password.hashCode()) : null;
    }

    public boolean checkPassword(String plainPassword) {
        return hashPassword(plainPassword).equals(this.passwordHash);
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public Role getRole() { return role; }

    public void persist() {
        new UserDAO().saveUser(this);
    }

    @Override
    public String toString() {
        return String.format("%s [ID: %s, Username: %s, Role: %s]",
                getClass().getSimpleName(), userId, username, role.getDisplayName());
    }
}