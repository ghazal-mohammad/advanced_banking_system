// src/main/java/com/bankingSystem/Account/statePattern/ClosedState.java
package com.bankingSystem.Account.statePattern;

import com.bankingSystem.Account.Account;

public class ClosedState implements AccountState {

    @Override
    public void deposit(Account account, double amount) {
        throw new IllegalStateException("Operation rejected: Account is CLOSED");
    }

    @Override
    public void withdraw(Account account, double amount) {
        throw new IllegalStateException("Operation rejected: Account is CLOSED");
    }

    @Override
    public void freeze(Account account) {
        throw new IllegalStateException("Cannot freeze a closed account");
    }

    @Override
    public void suspend(Account account) {
        throw new IllegalStateException("Cannot suspend a closed account");
    }

    @Override
    public void activate(Account account) {
        throw new IllegalStateException("Cannot reactivate a closed account");
    }

    @Override
    public void close(Account account) {
        System.out.println("Account is already closed");
    }
}