// src/main/java/com/bank/transaction/TransactionService.java
package com.bankingSystem.Transaction;

import com.bankingSystem.Account.Account;
import com.bankingSystem.Transaction.ChainOfResponsability.ApprovalHandler;
import com.bankingSystem.Transaction.ChainOfResponsability.LargeTransactionHandler;
import com.bankingSystem.Transaction.ChainOfResponsability.ManagerApprovalHandler;
import com.bankingSystem.Transaction.ChainOfResponsability.SmallTransactionHandler;

public class TransactionService {
    private ApprovalHandler approvalChain;

    public TransactionService() {
        buildApprovalChain();
    }

    private void buildApprovalChain() {
        approvalChain = new SmallTransactionHandler();
        ApprovalHandler large = new LargeTransactionHandler();
        ApprovalHandler manager = new ManagerApprovalHandler();

        approvalChain.setNext(large);
        large.setNext(manager);
    }

    // اجعل TransactionService هو المسؤول الوحيد عن تنفيذ المعاملة بعد الموافقة
    public void processTransaction(Account from, Account to, double amount, String type) {
        Transaction tx = new Transaction(type, amount,
                from != null ? from.getAccountNumber() : null,
                to != null ? to.getAccountNumber() : null);

        approvalChain.handle(tx);  // Chain of Responsibility

        if (tx.isApproved()) {
            if ("DEPOSIT".equals(type))      to.deposit(amount);
            if ("WITHDRAW".equals(type))     from.withdraw(amount);
            if ("TRANSFER".equals(type)) {
                from.withdraw(amount);
                to.deposit(amount);
            }

            tx.setStatus("COMPLETED");
            if (from != null) from.addTransaction(tx);
            if (to != null)   to.addTransaction(tx);
        }
    }
}