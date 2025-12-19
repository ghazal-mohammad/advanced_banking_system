package com.bankingSystem.Account.statePattern;

import com.bankingSystem.Account.Account;

public interface AccountState {
    void deposit(Account account, double amount);
    void withdraw(Account account, double amount);
    void freeze(Account account);
    void suspend(Account account);
    void activate(Account account);
    void close(Account account);
}