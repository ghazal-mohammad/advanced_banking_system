// src/main/java/com/bankingSystem/Transaction/TransactionService.java
package com.bankingSystem.Transaction;

import com.bankingSystem.Account.Account;
import com.bankingSystem.Database.TransactionDAO;
import com.bankingSystem.Transaction.ChainOfResponsability.ApprovalHandler;
import com.bankingSystem.Transaction.ChainOfResponsability.LargeTransactionHandler;
import com.bankingSystem.Transaction.ChainOfResponsability.ManagerApprovalHandler;
import com.bankingSystem.Transaction.ChainOfResponsability.SmallTransactionHandler;

public class TransactionService {

    private  ApprovalHandler approvalChain;
    private  TransactionDAO transactionDAO; // ← جديد: لحفظ المعاملات في الـ DB

    public TransactionService() {
        this.transactionDAO = new TransactionDAO(); // إنشاء مرة واحدة
        buildApprovalChain();
    }

    private void buildApprovalChain() {
        ApprovalHandler small = new SmallTransactionHandler();
        ApprovalHandler large = new LargeTransactionHandler();
        ApprovalHandler manager = new ManagerApprovalHandler();

        small.setNext(large);
        large.setNext(manager);
        this.approvalChain = small;
    }


    public void processTransaction(Account from, Account to, double amount, String type) {
        // إنشاء المعاملة
        Transaction tx = new Transaction(
                type,
                amount,
                from != null ? from.getAccountNumber() : null,
                to != null ? to.getAccountNumber() : null
        );

        // تطبيق سلسلة الموافقة
        approvalChain.handle(tx);

        // إذا تمت الموافقة → نفذ العملية
        if (tx.isApproved()) {
            executeOperation(tx, from, to);
            tx.setStatus("COMPLETED");

            // حفظ المعاملة في قاعدة البيانات أولاً
            transactionDAO.saveTransaction(tx);

            // إضافة المعاملة إلى تاريخ الحسابات + حفظ الحسابات في الـ DB
            if (from != null) {
                from.addTransaction(tx);
                from.persist();  // ← يستدعي AccountDAO.saveAccount(this)
            }
            if (to != null) {
                to.addTransaction(tx);
                to.persist();    // ← يستدعي AccountDAO.saveAccount(this)
            }

            System.out.println("Transaction COMPLETED & PERSISTED: " + tx);
        }
        // إذا كانت تنتظر موافقة المدير
        else if (tx.isPendingManagerApproval()) {
            transactionDAO.saveTransaction(tx); // ← نحفظها حتى لو معلقة
            System.out.println("Transaction PENDING MANAGER APPROVAL → Saved with ID: " + tx.getTransactionId());
        }
        // إذا تم رفضها
        else {
            transactionDAO.saveTransaction(tx); // اختياري: نحفظ حتى المرفوضة للـ audit
            System.out.println("Transaction REJECTED: " + tx.getStatus());
        }
    }

    private void executeOperation(Transaction tx, Account from, Account to) {
        switch (tx.getType()) {
            case "DEPOSIT" -> to.deposit(tx.getAmount());
            case "WITHDRAW" -> from.withdraw(tx.getAmount());
            case "TRANSFER" -> {
                from.withdraw(tx.getAmount());
                to.deposit(tx.getAmount());
            }
            default -> throw new IllegalArgumentException("Unknown transaction type: " + tx.getType());
        }
    }

    // === طريقة إضافية للموافقة اليدوية من المدير (تُستدعى من Bank) ===
//    public void approvePendingTransaction(String transactionId, boolean approve) {
//        Transaction tx = transactionDAO.loadTransactions(null).stream()
//                .filter(t -> t.getTransactionId().equals(transactionId))
//                .findFirst()
//                .orElse(null);
//        if (tx == null || !tx.isPendingManagerApproval()) {
//            System.out.println("Transaction not found or not pending approval.");
//            return;
//        }
//        if (approve) {
//            tx.setStatus("APPROVED");
//            S from = tx.getFromAccount() ;
//            Account to = tx.getToAccount() != null ? Bank.getInstance().getAccount(tx.getToAccount()) : null;
//            executeOperation(tx, from, to);
//            tx.setStatus("COMPLETED");
//
//            if (from != null) { from.addTransaction(tx); from.persist(); }
//            if (to != null)   { to.addTransaction(tx);   to.persist(); }
//
//            transactionDAO.saveTransaction(tx); // تحديث الحالة في الـ DB
//            System.out.println("Manager APPROVED & EXECUTED transaction: " + transactionId);
//        } else {
//            tx.setStatus("REJECTED_BY_MANAGER");
//            transactionDAO.saveTransaction(tx);
//            System.out.println("Manager REJECTED transaction: " + transactionId);
//        }
//    }
}