// src/main/java/com/bankingSystem/Account/LoanAccount.java
package com.bankingSystem.Account;

import com.bankingSystem.Interest.LoanInterest;
import com.bankingSystem.Transaction.Transaction;

public class LoanAccount extends Account {
    public double loanAmount;

    public LoanAccount(String accountNumber, String ownerId, double loanAmount) {
        super(accountNumber, ownerId);
        this.loanAmount = loanAmount;
        this.balance = -loanAmount; // Negative balance for loan
        this.interestStrategy = new LoanInterest(); // Strategy Pattern
    }

    @Override
    public double calculateInterest() {
        return interestStrategy.calculateInterest(balance); // Return double
    }

    public void makePayment(double amount) {
        if (amount > 0) {
            balance += amount;
            addTransaction(new Transaction("Loan Payment", amount));
            notifyObservers("Loan payment: " + amount);
        }
    }
}