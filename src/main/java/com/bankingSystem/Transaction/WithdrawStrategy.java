package com.bankingSystem.Transaction;

// WithdrawStrategy.java


import com.bankingSystem.Account.Account;

public class WithdrawStrategy implements TransactionStrategy {
    @Override
    public void execute(Account from, Account to, double amount, Transaction transaction) {
        from.withdraw(amount);
        transaction.setStatus("COMPLETED");
        from.addTransaction(transaction);
        System.out.println("Withdrawal completed: -" + amount + " from " + from.getAccountNumber());
    }
}
