// src/main/java/com/bank/transaction/TransactionStrategy.java
package com.bankingSystem.Transaction;

import com.bankingSystem.Account.Account;

public interface TransactionStrategy {
    void execute(Account from, Account to, double amount, Transaction transaction);
}