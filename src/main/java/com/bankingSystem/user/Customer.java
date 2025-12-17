// src/main/java/com/bankingSystem/Roles/Customer.java
package com.bankingSystem.user;

public class Customer extends User {
    public Customer(String username, String password) {
        super(username, password, Role.CUSTOMER);
    }

    // For loading from DB
    public Customer(String userId, String username, String passwordHash) {
        super(userId, username, passwordHash, Role.CUSTOMER);
    }
}