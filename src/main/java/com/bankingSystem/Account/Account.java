// src/main/java/com/bankingSystem/Account/Account.java
package com.bankingSystem.Account;

import com.bankingSystem.Account.CompositePattern.AccountComponent;
import com.bankingSystem.Account.statePattern.AccountState;
import com.bankingSystem.Account.statePattern.ActiveState;
import com.bankingSystem.Interest.InterestStrategy;
import com.bankingSystem.Transaction.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Account implements AccountComponent {
    protected String accountId;
    protected String accountNumber;
    protected double balance;
    protected LocalDateTime creationDate;
    protected String ownerId;
    protected AccountState state;
    protected InterestStrategy interestStrategy;
    protected List<Transaction> transactionHistory = new ArrayList<>();

    public Account(String accountNumber, String ownerId) {
        this.accountId = UUID.randomUUID().toString();
        this.accountNumber = accountNumber;
        this.ownerId = ownerId;
        this.balance = 0.0;
        this.creationDate = LocalDateTime.now();
        this.state = new ActiveState();
    }

    // Abstract method – يجب override في الـ subclasses لترجع double
    public abstract double calculateInterest();

    // State Pattern methods
    public void deposit(double amount) {
        state.deposit(this, amount);
    }

    public void withdraw(double amount) {
        state.withdraw(this, amount);
    }

    public void freeze() { state.freeze(this); }
    public void suspend() { state.suspend(this); }
    public void activate() { state.activate(this); }
    public void close() { state.close(this); }

    // Transaction methods (بدل addToHistory)
    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }

    // Observer stub (للـ Notification – يمكن توسيع لاحقًا)
    public void notifyObservers(String message) {
        // هنا نضيف Observer Pattern لاحقًا
        System.out.println("Notification: " + message);
    }

    // Getters/Setters
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public void setState(AccountState state) { this.state = state; }
    public List<Transaction> getTransactionHistory() { return new ArrayList<>(transactionHistory); }
    public void setInterestStrategy(InterestStrategy strategy) { this.interestStrategy = strategy; }

    // Composite methods
    public void showDetails() {
        System.out.printf("%s | %s | Balance: %.2f | Status: %s%n",
                getClass().getSimpleName(), accountNumber, balance, state.getClass().getSimpleName().replace("State", ""));
    }

    public double getTotalBalance() { return balance; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + accountNumber + "] Balance: " + balance;
    }
}