// src/main/java/com/bankingSystem/Account/CheckingAccount.java
package com.bankingSystem.Account;

public class CheckingAccount extends Account {
    private double overdraftLimit = 500.0;

    public CheckingAccount(String accountNumber, String ownerId) {
        super(accountNumber, ownerId);
    }

    public boolean canOverdraft(double amount) {
        return (getBalance() + overdraftLimit) >= amount;
    }

    @Override
    public double calculateInterest() {
        return 0.0; // Checking accounts usually have no interest
    }

    @Override
    public String toString() {
        return "Checking" + super.toString() + String.format(" | Overdraft Limit: %.2f", overdraftLimit);
    }
}