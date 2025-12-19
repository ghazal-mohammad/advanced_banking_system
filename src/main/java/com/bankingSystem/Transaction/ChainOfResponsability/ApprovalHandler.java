// src/main/java/com/bank/approval/ApprovalHandler.java
package com.bankingSystem.Transaction.ChainOfResponsability;


import com.bankingSystem.Transaction.Transaction;

public abstract class ApprovalHandler {
    protected ApprovalHandler nextHandler;

    public void setNext(ApprovalHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract void handle(Transaction transaction);

    protected void passToNext(Transaction transaction) {
        if (nextHandler != null) {
            nextHandler.handle(transaction);
        }
//        else {
//            transaction.setStatus("APPROVED - Auto-approved (small amount)");
//            System.out.println("Transaction auto-approved (≤ 5000)");
//        }
    }
}