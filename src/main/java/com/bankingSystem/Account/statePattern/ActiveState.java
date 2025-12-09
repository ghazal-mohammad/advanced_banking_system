// وأخيرًا نعدل ActiveState.java عشان يكون كامل ومتوازن
// ActiveState.java (محدث ومكتمل)
package com.bankingSystem.Account.statePattern;


import com.bankingSystem.Account.Account;
import com.bankingSystem.Account.CheckingAccount;
import com.bankingSystem.Transaction.Transaction;

public class ActiveState implements AccountState {

    @Override
    public void deposit(Account account, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive");
        account.setBalance(account.getBalance() + amount);
        account.addTransaction(new Transaction("Deposit", amount));
        System.out.printf("Deposit successful: +%.2f | New balance: %.2f%n", amount, account.getBalance());
    }

    @Override
    public void withdraw(Account account, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive");

        // دعم Overdraft للحساب الجاري فقط
        if (account instanceof CheckingAccount checking) {
            if (checking.canOverdraft(amount)) {
                account.setBalance(account.getBalance() - amount);
                account.addTransaction(new Transaction("Withdrawal (Overdraft used)", amount));
                System.out.printf("Withdrawal successful (overdraft): -%.2f | Balance: %.2f%n", amount, account.getBalance());
                return;
            }
        }

        if (account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);
            account.addTransaction(new Transaction("Withdrawal", amount));
            System.out.printf("Withdrawal successful: -%.2f | New balance: %.2f%n", amount, account.getBalance());
        } else {
            throw new IllegalStateException("Insufficient funds");
        }
    }

    @Override
    public void freeze(Account account) {
        account.setState(new FrozenState());
        System.out.println("Account state changed: ACTIVE → FROZEN");
    }

    @Override
    public void suspend(Account account) {
        account.setState(new SuspendedState());
        System.out.println("Account state changed: ACTIVE → SUSPENDED");
    }

    @Override
    public void activate(Account account) {
        System.out.println("Account is already active");
    }

    @Override
    public void close(Account account) {
        account.setState(new ClosedState());
        System.out.println("Account state changed: ACTIVE → CLOSED");
    }
}