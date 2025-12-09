// src/main/java/com/bankingSystem/Account/StatePattern/FrozenState.java
package com.bankingSystem.Account.statePattern;

import com.bankingSystem.Account.Account;
import com.bankingSystem.Account.statePattern.AccountState;
import com.bankingSystem.Transaction.Transaction;

public class FrozenState implements AccountState {
    @Override
    public void deposit(Account account, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("المبلغ يجب أن يكون إيجابي");
        account.setBalance(account.getBalance() + amount);
        account.addTransaction(new Transaction("Deposit (Frozen)", amount)); // Fixed
        System.out.println("إيداع مسموح في الحساب المجمد: +" + amount);
    }

    @Override
    public void withdraw(Account account, double amount) {
        throw new IllegalStateException("لا يمكن السحب من الحساب المجمد");
    }

    @Override
    public void freeze(Account account) {
        System.out.println("الحساب مجمد بالفعل");
    }

    @Override
    public void suspend(Account account) {
        account.setState(new SuspendedState());
    }

    @Override
    public void activate(Account account) {
        account.setState(new ActiveState());
    }

    @Override
    public void close(Account account) {
        account.setState(new ClosedState());
    }
}