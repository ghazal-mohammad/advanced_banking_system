// src/main/java/com/bankingSystem/Roles/Admin.java
package com.bankingSystem.user;

public class Admin extends User {
    public Admin(String username, String password) {
        super(username, password, Role.ADMIN);
    }

    public Admin(String userId, String username, String passwordHash) {
        super(userId, username, passwordHash, Role.ADMIN);
    }

    // Admins can manage users, view all reports, etc.
}