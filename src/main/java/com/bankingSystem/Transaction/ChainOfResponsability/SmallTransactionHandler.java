// SmallTransactionHandler.java
package com.bankingSystem.Transaction.ChainOfResponsability;

import com.bankingSystem.Transaction.Transaction;

public class SmallTransactionHandler extends ApprovalHandler {
    @Override
    public void handle(Transaction transaction) {
        if (transaction.getAmount() <= 5000) {
            transaction.setStatus("APPROVED - Small transaction");
            System.out.println("Small transaction approved automatically");
        } else {
            passToNext(transaction);
        }
    }
}