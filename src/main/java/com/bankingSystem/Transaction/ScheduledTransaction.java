// src/main/java/com/bankingSystem/Transaction/ScheduledTransaction.java
package com.bankingSystem.Transaction;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * كلاس يمثل معاملة مجدولة يتم تنفيذها بشكل دوري
 * يستخدم Strategy Pattern لتنفيذ استراتيجيات السحب أو الإيداع
 */
public class ScheduledTransaction {
    private String scheduledTransactionId;
    private String transactionType; // "DEPOSIT" or "WITHDRAW"
    private double amount;
    private String fromAccountNumber; // null for DEPOSIT
    private String toAccountNumber; // null for WITHDRAW
    private String strategyType; // "DEPOSIT" or "WITHDRAW"
    private long intervalMinutes; // الفترة الزمنية بين كل تنفيذ (بالدقائق)
    private LocalDateTime nextExecutionTime;
    private LocalDateTime createdAt;
    private boolean isActive;
    private String createdBy; // User ID who created this scheduled transaction
    private LocalDateTime lastExecutionTime;
    private int executionCount; // عدد مرات التنفيذ

    public ScheduledTransaction(String transactionType, double amount,
                                String fromAccountNumber, String toAccountNumber,
                                long intervalMinutes, String createdBy) {
        this.scheduledTransactionId = UUID.randomUUID().toString();
        this.transactionType = transactionType;
        this.amount = amount;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.strategyType = transactionType; // DEPOSIT uses DepositStrategy, WITHDRAW uses WithdrawStrategy
        this.intervalMinutes = intervalMinutes;
        this.createdAt = LocalDateTime.now();
        this.nextExecutionTime = LocalDateTime.now().plusMinutes(intervalMinutes);
        this.isActive = true;
        this.createdBy = createdBy;
        this.lastExecutionTime = null;
        this.executionCount = 0;
    }

    // Constructor for loading from database
    public ScheduledTransaction(String scheduledTransactionId, String transactionType, double amount,
                                String fromAccountNumber, String toAccountNumber, String strategyType,
                                long intervalMinutes, LocalDateTime nextExecutionTime,
                                LocalDateTime createdAt, boolean isActive, String createdBy,
                                LocalDateTime lastExecutionTime, int executionCount) {
        this.scheduledTransactionId = scheduledTransactionId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.strategyType = strategyType;
        this.intervalMinutes = intervalMinutes;
        this.nextExecutionTime = nextExecutionTime;
        this.createdAt = createdAt;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.lastExecutionTime = lastExecutionTime;
        this.executionCount = executionCount;
    }

    /**
     * تحديث وقت التنفيذ القادم بعد تنفيذ المعاملة
     */
    public void updateNextExecutionTime() {
        this.lastExecutionTime = LocalDateTime.now();
        this.nextExecutionTime = LocalDateTime.now().plusMinutes(intervalMinutes);
        this.executionCount++;
    }

    /**
     * التحقق من أنه حان وقت التنفيذ
     */
    public boolean isReadyToExecute() {
        return isActive && LocalDateTime.now().isAfter(nextExecutionTime);
    }

    // Getters and Setters
    public String getScheduledTransactionId() {
        return scheduledTransactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public long getIntervalMinutes() {
        return intervalMinutes;
    }

    public LocalDateTime getNextExecutionTime() {
        return nextExecutionTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getLastExecutionTime() {
        return lastExecutionTime;
    }

    public int getExecutionCount() {
        return executionCount;
    }

    @Override
    public String toString() {
        return String.format(
                "[%s] Scheduled %s | Amount: %.2f | From: %s | To: %s | " +
                        "Interval: %d min | Next: %s | Active: %s | Executions: %d",
                scheduledTransactionId.substring(0, 8),
                transactionType,
                amount,
                fromAccountNumber != null ? fromAccountNumber : "-",
                toAccountNumber != null ? toAccountNumber : "-",
                intervalMinutes,
                nextExecutionTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                isActive ? "Yes" : "No",
                executionCount
        );
    }
}
