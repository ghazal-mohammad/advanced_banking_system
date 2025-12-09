// src/main/java/com/bankingSystem/Account/SavingsAccount.java
package com.bankingSystem.Account;

import com.bankingSystem.Interest.SavingInterest;

public class SavingsAccount extends Account {
    public SavingsAccount(String accountNumber, String ownerId) {
        super(accountNumber, ownerId);
        this.interestStrategy = new SavingInterest(); // Strategy Pattern
    }

    @Override
    public double calculateInterest() {
        return interestStrategy.calculateInterest(balance); // يرجع double
    }
}