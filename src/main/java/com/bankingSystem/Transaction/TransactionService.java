// src/main/java/com/bankingSystem/Transaction/TransactionService.java
package com.bankingSystem.Transaction;

import com.bankingSystem.Account.Account;
import com.bankingSystem.Database.AccountDAO;
import com.bankingSystem.Database.TransactionDAO;
import com.bankingSystem.Transaction.ChainOfResponsability.*;

public class TransactionService {
    private ApprovalHandler approvalChain;
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    public TransactionService() {
        buildApprovalChain();
    }

    private void buildApprovalChain() {
        ApprovalHandler small = new SmallTransactionHandler();
        ApprovalHandler large = new LargeTransactionHandler();
        ApprovalHandler manager = new ManagerApprovalHandler();

        small.setNext(large);
        large.setNext(manager);
        approvalChain = small;
    }

    /**
     * Main method called from demo for deposits, withdrawals, transfers
     */
    public void processTransaction(Account from, Account to, double amount, String type) {
        Transaction tx = new Transaction(type, amount,
                from != null ? from.getAccountNumber() : null,
                to != null ? to.getAccountNumber() : null);

        // Run approval chain
        approvalChain.handle(tx);

        if (tx.isApproved()) {
            // === EXECUTE THE MONEY MOVEMENT ===
            switch (type) {
                case "DEPOSIT" -> to.deposit(amount);
                case "WITHDRAW" -> from.withdraw(amount);
                case "TRANSFER" -> {
                    from.withdraw(amount);
                    to.deposit(amount);
                }
                default -> throw new IllegalArgumentException("Unknown transaction type: " + type);
            }

            tx.setStatus("COMPLETED");

            // Add to in-memory history
            if (from != null) from.addTransaction(tx);
            if (to != null) to.addTransaction(tx);

            // === PERSIST ACCOUNTS WITH NEW BALANCE ===
            if (from != null) from.persist();
            if (to != null) to.persist();

            System.out.println("Transaction COMPLETED and balances updated: " + tx.getDescription());
        } else {
            System.out.println("Transaction requires manager approval: " + tx.getStatus());
        }

        // === ALWAYS SAVE THE TRANSACTION RECORD (for audit and pending approval) ===
        transactionDAO.saveTransaction(tx);
    }

    /**
     * Called by Manager.approveTransaction() to execute pending large transactions
     */
    public void executeApprovedTransaction(String transactionId) {
        Transaction tx = transactionDAO.loadTransactionById(transactionId);

        if (tx == null || !"APPROVED_BY_MANAGER".equals(tx.getStatus())) {
            System.out.println("Cannot execute: transaction not found or not approved.");
            return;
        }

        Account from = tx.getFromAccount() != null ? accountDAO.loadAccount(tx.getFromAccount()) : null;
        Account to = tx.getToAccount() != null ? accountDAO.loadAccount(tx.getToAccount()) : null;

        try {
            switch (tx.getType()) {
                case "DEPOSIT" -> to.deposit(tx.getAmount());
                case "WITHDRAW" -> from.withdraw(tx.getAmount());
                case "TRANSFER" -> {
                    if (from.getBalance() < tx.getAmount()) {
                        throw new IllegalStateException("Insufficient funds");
                    }
                    from.withdraw(tx.getAmount());
                    to.deposit(tx.getAmount());
                }
            }

            tx.setStatus("COMPLETED");

            if (from != null) {
                from.addTransaction(tx);
                from.persist();
            }
            if (to != null) {
                to.addTransaction(tx);
                to.persist();
            }

            // Update status in DB (don't re-insert whole object)
            transactionDAO.updateTransactionStatus(transactionId, "COMPLETED");

            System.out.println("Large transaction EXECUTED successfully: " + transactionId);
        } catch (Exception e) {
            transactionDAO.updateTransactionStatus(transactionId, "FAILED: " + e.getMessage());
            System.out.println("Execution failed: " + e.getMessage());
        }
    }
}
//package com.bankingSystem.Transaction;
//
//import com.bankingSystem.Account.Account;
//import com.bankingSystem.Database.TransactionDAO;
//import com.bankingSystem.Transaction.ChainOfResponsability.ApprovalHandler;
//import com.bankingSystem.Transaction.ChainOfResponsability.LargeTransactionHandler;
//import com.bankingSystem.Transaction.ChainOfResponsability.ManagerApprovalHandler;
//import com.bankingSystem.Transaction.ChainOfResponsability.SmallTransactionHandler;
//
//public class TransactionService {
//
//    private  ApprovalHandler approvalChain;
//    private  TransactionDAO transactionDAO; // ← جديد: لحفظ المعاملات في الـ DB
//
//    public TransactionService() {
//        this.transactionDAO = new TransactionDAO(); // إنشاء مرة واحدة
//        buildApprovalChain();
//    }
//
//    private void buildApprovalChain() {
//        ApprovalHandler small = new SmallTransactionHandler();
//        ApprovalHandler large = new LargeTransactionHandler();
//        ApprovalHandler manager = new ManagerApprovalHandler();
//
//        small.setNext(large);
//        large.setNext(manager);
//        this.approvalChain = small;
//    }
//
//
//    public void processTransaction(Account from, Account to, double amount, String type) {
//        // إنشاء المعاملة
//        Transaction tx = new Transaction(
//                type,
//                amount,
//                from != null ? from.getAccountNumber() : null,
//                to != null ? to.getAccountNumber() : null
//        );
//
//        // تطبيق سلسلة الموافقة
//        approvalChain.handle(tx);
//
//        // إذا تمت الموافقة → نفذ العملية
//        if (tx.isApproved()) {
//            executeOperation(tx, from, to);
//            tx.setStatus("COMPLETED");
//
//            // حفظ المعاملة في قاعدة البيانات أولاً
//            transactionDAO.saveTransaction(tx);
//
//            // إضافة المعاملة إلى تاريخ الحسابات + حفظ الحسابات في الـ DB
//            if (from != null) {
//                from.addTransaction(tx);
//                from.persist();  // ← يستدعي AccountDAO.saveAccount(this)
//            }
//            if (to != null) {
//                to.addTransaction(tx);
//                to.persist();    // ← يستدعي AccountDAO.saveAccount(this)
//            }
//
//            System.out.println("Transaction COMPLETED & PERSISTED: " + tx);
//        }
//        // إذا كانت تنتظر موافقة المدير
//        else if (tx.isPendingManagerApproval()) {
//            transactionDAO.saveTransaction(tx); // ← نحفظها حتى لو معلقة
//            System.out.println("Transaction PENDING MANAGER APPROVAL → Saved with ID: " + tx.getTransactionId());
//        }
//        // إذا تم رفضها
//        else {
//            transactionDAO.saveTransaction(tx); // اختياري: نحفظ حتى المرفوضة للـ audit
//            System.out.println("Transaction REJECTED: " + tx.getStatus());
//        }
//    }
//
//    private void executeOperation(Transaction tx, Account from, Account to) {
//        switch (tx.getType()) {
//            case "DEPOSIT" -> to.deposit(tx.getAmount());
//            case "WITHDRAW" -> from.withdraw(tx.getAmount());
//            case "TRANSFER" -> {
//                from.withdraw(tx.getAmount());
//                to.deposit(tx.getAmount());
//            }
//            default -> throw new IllegalArgumentException("Unknown transaction type: " + tx.getType());
//        }
//    }

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
//}