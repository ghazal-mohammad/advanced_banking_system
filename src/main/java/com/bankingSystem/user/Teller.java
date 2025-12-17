// src/main/java/com/bankingSystem/Roles/Teller.java
package com.bankingSystem.user;

public class Teller extends User {
    public Teller(String username, String password) {
        super(username, password, Role.TELLER);
    }

    public Teller(String userId, String username, String passwordHash) {
        super(userId, username, passwordHash, Role.TELLER);
    }
}