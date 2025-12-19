package com.bankingSystem.Account.DecoratorPattern;

import com.bankingSystem.Account.Account;

// Abstract Decorator class
public abstract class AccountDecorator extends Account {
    protected Account decoratedAccount; // Reference to the decorated account

    // Pass account properties correctly
    public AccountDecorator(Account account) {
        super(account.accountNumber, account.ownerId); // استدعاء البيانات الأساسية من الحساب
        this.decoratedAccount = account;
    }

    @Override
    public void getDetails() {
        decoratedAccount.getDetails(); // Call original method
    }

    @Override
    public void performOperation(String operationType) {
        decoratedAccount.performOperation(operationType); // Call original method
    }

    @Override
    public double calculateInterest() {
        return decoratedAccount.calculateInterest(); // Call original interest method
    }
}