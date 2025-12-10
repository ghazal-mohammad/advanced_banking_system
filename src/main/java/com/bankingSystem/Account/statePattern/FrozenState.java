// src/main/java/com/bankingSystem/Account/statePattern/FrozenState.java
package com.bankingSystem.Account.statePattern;

import com.bankingSystem.Account.Account;
import com.bankingSystem.Transaction.Transaction;

public class FrozenState implements AccountState {
    @Override
    public void deposit(Account account, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive");
        account.setBalance(account.getBalance() + amount);
        account.addTransaction(new Transaction("Deposit (Frozen)", amount)); // Ensured addition
        System.out.println("Deposit allowed in frozen account: +" + amount);
    }

    @Override
    public void withdraw(Account account, double amount) {
        throw new IllegalStateException("Cannot withdraw from frozen account");
    }

    @Override
    public void freeze(Account account) {
        System.out.println("Account is already frozen");
    }

    @Override
    public void suspend(Account account) {
        account.setState(new SuspendedState());
        System.out.println("Account state changed: FROZEN → SUSPENDED"); // Added transition
    }

    @Override
    public void activate(Account account) {
        account.setState(new ActiveState());
        System.out.println("Account state changed: FROZEN → ACTIVE");
    }

    @Override
    public void close(Account account) {
        account.setState(new ClosedState());
        System.out.println("Account state changed: FROZEN → CLOSED");
    }
}