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

    public void processTransaction(TransactionStrategy strategy, Account from, Account to, double amount) {
        Transaction tx = new Transaction(strategy instanceof TransferStrategy ? "TRANSFER" :
                strategy instanceof DepositStrategy ? "DEPOSIT" : "WITHDRAW", amount);

        if (from != null) tx = new Transaction(tx.getType(), amount, from.getAccountNumber(), to != null ? to.getAccountNumber() : null);

        // Chain of Responsibility
        approvalChain.handle(tx);

        if (tx.getStatus().contains("APPROVED") || tx.getStatus().contains("COMPLETED")) {
            strategy.execute(from, to, amount, tx);
            if (from != null) from.addTransaction(tx);
            if (to != null && strategy instanceof DepositStrategy) to.addTransaction(tx);
        } else {
            System.out.println("Transaction blocked: " + tx.getStatus());
        }
    }
}