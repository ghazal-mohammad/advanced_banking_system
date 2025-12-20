// src/main/java/com/bankingSystem/Transaction/ScheduledTransactionService.java
package com.bankingSystem.Transaction;

import com.bankingSystem.Account.Account;
import com.bankingSystem.Database.AccountDAO;
import com.bankingSystem.Database.ScheduledTransactionDAO;
import com.bankingSystem.Database.TransactionDAO;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service لإدارة وتنفيذ المعاملات المجدولة بشكل دوري
 * يستخدم:
 * - Singleton Pattern: مثيل واحد فقط من الخدمة
 * - Strategy Pattern: استخدام DepositStrategy و WithdrawStrategy الموجودة
 * - ScheduledExecutorService: للتنفيذ الدوري
 */
public class ScheduledTransactionService {

    // ✅ Singleton Pattern
    private static ScheduledTransactionService instance;

    private final ScheduledTransactionDAO scheduledTransactionDAO;
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;
    // ✅ Strategy Pattern: استخدام الاستراتيجيات الموجودة
    private final DepositStrategy depositStrategy;
    private final WithdrawStrategy withdrawStrategy;
    private ScheduledExecutorService executorService;
    private boolean isRunning = false;

    private ScheduledTransactionService() {
        this.scheduledTransactionDAO = new ScheduledTransactionDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        // ✅ Strategy Pattern: استخدام الاستراتيجيات الموجودة
        this.depositStrategy = new DepositStrategy();
        this.withdrawStrategy = new WithdrawStrategy();
    }

    public static synchronized ScheduledTransactionService getInstance() {
        if (instance == null) {
            instance = new ScheduledTransactionService();
        }
        return instance;
    }

    /**
     * بدء خدمة المعاملات المجدولة
     * تفحص كل دقيقة المعاملات المجدولة وتنفذ التي حان وقتها
     */
    public void start() {
        if (isRunning) {
            System.out.println("Scheduled Transaction Service is already running.");
            return;
        }

        isRunning = true;
        executorService = Executors.newSingleThreadScheduledExecutor();

        // تشغيل الفحص كل دقيقة
        executorService.scheduleAtFixedRate(
                this::executeReadyScheduledTransactions,
                0,  // Start immediately
                1,  // Check every 1 minute
                TimeUnit.MINUTES
        );

        System.out.println("Scheduled Transaction Service started. Checking every minute.");
    }

    /**
     * إيقاف خدمة المعاملات المجدولة
     */
    public void stop() {
        if (!isRunning) {
            return;
        }

        isRunning = false;
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Scheduled Transaction Service stopped.");
    }

    /**
     * تنفيذ المعاملات المجدولة التي حان وقتها
     * يستخدم Strategy Pattern لتنفيذ الاستراتيجيات المختلفة
     */
    private void executeReadyScheduledTransactions() {
        try {
            List<ScheduledTransaction> activeScheduledTransactions =
                    scheduledTransactionDAO.loadActiveScheduledTransactions();

            for (ScheduledTransaction scheduledTx : activeScheduledTransactions) {
                if (scheduledTx.isReadyToExecute()) {
                    executeScheduledTransaction(scheduledTx);
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing scheduled transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * تنفيذ معاملة مجدولة واحدة
     * يستخدم Strategy Pattern حسب نوع المعاملة
     */
    private void executeScheduledTransaction(ScheduledTransaction scheduledTx) {
        try {
            String transactionType = scheduledTx.getTransactionType();
            double amount = scheduledTx.getAmount();
            String fromAccountNumber = scheduledTx.getFromAccountNumber();
            String toAccountNumber = scheduledTx.getToAccountNumber();

            Account fromAccount = null;
            Account toAccount = null;

            // تحميل الحسابات المطلوبة
            if (fromAccountNumber != null) {
                fromAccount = accountDAO.loadAccount(fromAccountNumber);
                if (fromAccount == null) {
                    System.err.println("Cannot execute scheduled transaction: From account not found: " + fromAccountNumber);
                    return;
                }
            }

            if (toAccountNumber != null) {
                toAccount = accountDAO.loadAccount(toAccountNumber);
                if (toAccount == null) {
                    System.err.println("Cannot execute scheduled transaction: To account not found: " + toAccountNumber);
                    return;
                }
            }

            // ✅ استخدام Strategy Pattern للتنفيذ (استخدام DepositStrategy أو WithdrawStrategy)
            Transaction transaction = new Transaction(
                    transactionType,
                    amount,
                    fromAccountNumber,
                    toAccountNumber,
                    scheduledTx.getCreatedBy() != null ? scheduledTx.getCreatedBy() : "SYSTEM"
            );

            switch (transactionType) {
                case "DEPOSIT":
                    if (toAccount != null) {
                        // ✅ استخدام DepositStrategy
                        depositStrategy.execute(null, toAccount, amount, transaction);
                        toAccount.persist();
                    }
                    break;

                case "WITHDRAW":
                    if (fromAccount != null) {
                        // ✅ استخدام WithdrawStrategy
                        withdrawStrategy.execute(fromAccount, null, amount, transaction);
                        fromAccount.persist();
                    }
                    break;

                default:
                    System.err.println("Unknown transaction type for scheduled transaction: " + transactionType);
                    return;
            }

            // حفظ المعاملة في قاعدة البيانات للتدقيق
            transactionDAO.saveTransaction(transaction);

            // تحديث وقت التنفيذ القادم
            scheduledTx.updateNextExecutionTime();
            scheduledTransactionDAO.saveScheduledTransaction(scheduledTx);

            System.out.println("Scheduled transaction executed: " + scheduledTx.getScheduledTransactionId().substring(0, 8) +
                    " | Type: " + transactionType + " | Amount: " + amount);

        } catch (Exception e) {
            System.err.println("Error executing scheduled transaction " + scheduledTx.getScheduledTransactionId() +
                    ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * إنشاء معاملة مجدولة جديدة
     */
    public ScheduledTransaction createScheduledTransaction(String transactionType, double amount,
                                                           String fromAccountNumber, String toAccountNumber,
                                                           long intervalMinutes, String createdBy) {
        ScheduledTransaction scheduledTx = new ScheduledTransaction(
                transactionType,
                amount,
                fromAccountNumber,
                toAccountNumber,
                intervalMinutes,
                createdBy
        );

        scheduledTransactionDAO.saveScheduledTransaction(scheduledTx);
        System.out.println("Scheduled transaction created: " + scheduledTx.getScheduledTransactionId());

        return scheduledTx;
    }

    /**
     * الحصول على جميع المعاملات المجدولة
     */
    public List<ScheduledTransaction> getAllScheduledTransactions() {
        return scheduledTransactionDAO.loadAllScheduledTransactions();
    }

    /**
     * الحصول على المعاملات المجدولة النشطة فقط
     */
    public List<ScheduledTransaction> getActiveScheduledTransactions() {
        return scheduledTransactionDAO.loadActiveScheduledTransactions();
    }

    /**
     * إيقاف معاملة مجدولة (تعطيلها)
     */
    public void deactivateScheduledTransaction(String scheduledTransactionId) {
        scheduledTransactionDAO.updateScheduledTransactionStatus(scheduledTransactionId, false);
        System.out.println("Scheduled transaction deactivated: " + scheduledTransactionId);
    }

    /**
     * تفعيل معاملة مجدولة
     */
    public void activateScheduledTransaction(String scheduledTransactionId) {
        scheduledTransactionDAO.updateScheduledTransactionStatus(scheduledTransactionId, true);
        System.out.println("Scheduled transaction activated: " + scheduledTransactionId);
    }

    /**
     * حذف معاملة مجدولة
     */
    public void deleteScheduledTransaction(String scheduledTransactionId) {
        scheduledTransactionDAO.deleteScheduledTransaction(scheduledTransactionId);
        System.out.println("Scheduled transaction deleted: " + scheduledTransactionId);
    }

    /**
     * التحقق من حالة الخدمة
     */
    public boolean isRunning() {
        return isRunning;
    }
}
