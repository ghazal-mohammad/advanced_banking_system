// LargeTransactionHandler.java
package com.bankingSystem.Transaction.ChainOfResponsability;

import com.bankingSystem.Transaction.Transaction;

public class LargeTransactionHandler extends ApprovalHandler {
    @Override
    public void handle(Transaction transaction) {
        if (transaction.getAmount() <= 50000) {
            transaction.setStatus("APPROVED - Teller approved");
            System.out.println("Large transaction (≤50k) approved by Teller");
        } else {
            passToNext(transaction);
        }
    }
}