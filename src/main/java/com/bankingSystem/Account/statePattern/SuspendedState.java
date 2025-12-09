package com.bankingSystem.Account.statePattern;

// SuspendedState.java


import com.bankingSystem.Account.Account;

public class SuspendedState implements AccountState {

    @Override
    public void deposit(Account account, double amount) {
        throw new IllegalStateException("All operations blocked: Account is SUSPENDED");
    }

    @Override
    public void withdraw(Account account, double amount) {
        throw new IllegalStateException("All operations blocked: Account is SUSPENDED");
    }

    @Override
    public void freeze(Account account) {
        account.setState(new FrozenState());
        System.out.println("Account state changed: SUSPENDED → FROZEN");
    }

    @Override
    public void suspend(Account account) {
        System.out.println("Account is already suspended");
    }

    @Override
    public void activate(Account account) {
        account.setState(new ActiveState());
        System.out.println("Account state changed: SUSPENDED → ACTIVE");
    }

    @Override
    public void close(Account account) {
        account.setState(new ClosedState());
        System.out.println("Account state changed: SUSPENDED → CLOSED");
    }
}
