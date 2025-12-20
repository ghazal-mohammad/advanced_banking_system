// src/main/java/com/bankingSystem/Facade/AdministrativeDashboardFacade.java
package com.bankingSystem.Facade;

import com.bankingSystem.Database.TransactionDAO;
import com.bankingSystem.Transaction.Transaction;
import com.bankingSystem.Transaction.TransactionService;

import java.util.List;

/**
 * Facade Pattern: يوفر واجهة موحدة ومبسطة للوحة التحكم الإدارية
 * يعرض سجلات التدقيق (Audit Logs) وعدد المعاملات المتزامنة
 */
public class AdministrativeDashboardFacade {

    // ✅ Singleton Pattern
    private static AdministrativeDashboardFacade instance;

    private final TransactionDAO transactionDAO;
    private final TransactionService transactionService;

    private AdministrativeDashboardFacade() {
        this.transactionDAO = new TransactionDAO();
        this.transactionService = TransactionService.getInstance();
    }

    public static synchronized AdministrativeDashboardFacade getInstance() {
        if (instance == null) {
            instance = new AdministrativeDashboardFacade();
        }
        return instance;
    }

    /**
     * الحصول على سجلات التدقيق (Audit Logs) - جميع المعاملات
     */
    public List<Transaction> getAuditLogs() {
        return transactionDAO.loadAllTransactions();
    }

    /**
     * الحصول على سجلات التدقيق لفترة معينة
     */
    public List<Transaction> getAuditLogsByDateRange(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return transactionDAO.loadAllTransactions().stream()
                .filter(tx -> {
                    java.time.LocalDateTime performedAt = tx.getPerformedAt();
                    if (performedAt == null) performedAt = tx.getTimestamp();
                    return performedAt != null &&
                            !performedAt.isBefore(start) &&
                            !performedAt.isAfter(end);
                })
                .toList();
    }

    /**
     * الحصول على عدد المعاملات المتزامنة الحالية
     */
    public int getConcurrentTransactionCount() {
        return transactionService.getConcurrentTransactionCount();
    }

    /**
     * عرض معلومات لوحة التحكم الإدارية
     */
    public void displayDashboard() {
        System.out.println("\n==========================================");
        System.out.println("    Administrative Dashboard");
        System.out.println("==========================================");

        int concurrentCount = getConcurrentTransactionCount();
        System.out.println("Concurrent Transactions: " + concurrentCount);

        List<Transaction> allTransactions = getAuditLogs();
        System.out.println("Total Transactions: " + allTransactions.size());

        System.out.println("\n--- Recent Audit Logs (Last 10) ---");
        allTransactions.stream()
                .limit(10)
                .forEach(tx -> System.out.println(tx));

        System.out.println("==========================================\n");
    }

    /**
     * الحصول على ملخص سجلات التدقيق
     */
    public DashboardSummary getDashboardSummary() {
        List<Transaction> allTransactions = getAuditLogs();

        long completed = allTransactions.stream()
                .filter(tx -> "COMPLETED".equals(tx.getStatus()))
                .count();

        long pending = allTransactions.stream()
                .filter(tx -> "PENDING_MANAGER_APPROVAL".equals(tx.getStatus()))
                .count();

        double totalAmount = allTransactions.stream()
                .filter(tx -> "COMPLETED".equals(tx.getStatus()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        return new DashboardSummary(
                allTransactions.size(),
                completed,
                pending,
                totalAmount,
                getConcurrentTransactionCount()
        );
    }

    /**
     * Inner class for Dashboard Summary
     */
    public static class DashboardSummary {
        private final int totalTransactions;
        private final long completedTransactions;
        private final long pendingTransactions;
        private final double totalAmount;
        private final int concurrentTransactions;

        public DashboardSummary(int totalTransactions, long completedTransactions,
                                long pendingTransactions, double totalAmount,
                                int concurrentTransactions) {
            this.totalTransactions = totalTransactions;
            this.completedTransactions = completedTransactions;
            this.pendingTransactions = pendingTransactions;
            this.totalAmount = totalAmount;
            this.concurrentTransactions = concurrentTransactions;
        }

        public int getTotalTransactions() {
            return totalTransactions;
        }

        public long getCompletedTransactions() {
            return completedTransactions;
        }

        public long getPendingTransactions() {
            return pendingTransactions;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public int getConcurrentTransactions() {
            return concurrentTransactions;
        }

        @Override
        public String toString() {
            return String.format(
                    "Dashboard Summary:\n" +
                            "  Total Transactions: %d\n" +
                            "  Completed: %d\n" +
                            "  Pending: %d\n" +
                            "  Total Amount: %.2f\n" +
                            "  Concurrent Transactions: %d",
                    totalTransactions, completedTransactions, pendingTransactions,
                    totalAmount, concurrentTransactions
            );
        }
    }
}
