// DepositStrategy.java
package com.bankingSystem.Transaction;

import com.bankingSystem.Account.Account;

public class DepositStrategy implements TransactionStrategy {
    @Override
    public void execute(Account from, Account to, double amount, Transaction transaction) {
        to.deposit(amount);
        transaction.setStatus("COMPLETED");
        to.addTransaction(transaction);
        System.out.println("Deposit completed: +" + amount + " to " + to.getAccountNumber());
    }
}