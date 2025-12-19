// TransferStrategy.java
package com.bankingSystem.Transaction;

import com.bankingSystem.Account.Account;

public class TransferStrategy implements TransactionStrategy {
    @Override
    public void execute(Account from, Account to, double amount, Transaction transaction) {
        if (from.getBalance() < amount) {
            transaction.setStatus("REJECTED - Insufficient funds");
            throw new IllegalStateException("Insufficient funds for transfer");
        }

        from.withdraw(amount);
        to.deposit(amount);

        // سجلة معاملات منفصلة للتحويل
        Transaction outTx = new Transaction("TRANSFER_OUT", amount, from.getAccountNumber(), to.getAccountNumber());
        Transaction inTx = new Transaction("TRANSFER_IN", amount, from.getAccountNumber(), to.getAccountNumber());

        outTx.setStatus("COMPLETED");
        inTx.setStatus("COMPLETED");

        from.addTransaction(outTx);
        to.addTransaction(inTx);

        transaction.setStatus("COMPLETED");
        System.out.println("Transfer successful: " + amount + " from " + from.getAccountNumber() + " → " + to.getAccountNumber());
    }
}