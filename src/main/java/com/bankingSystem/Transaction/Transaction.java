// src/main/java/com/bank/transaction/Transaction.java
package com.bankingSystem.Transaction;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private String transactionId;
    private String type; // DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT, LOAN_PAYMENT, INTEREST
    private double amount;
    private LocalDateTime timestamp;
    private String fromAccount;
    private String toAccount;
    private String status; // PENDING, APPROVED, REJECTED, COMPLETED
    private String description;    // ====================== Original Constructors (unchanged) ======================


    public Transaction(String type, double amount) {
        this.transactionId = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
        this.description = type + " of " + String.format("%.2f", amount);
    }

    public Transaction(String type, double amount, String fromAccount, String toAccount) {
        this(type, amount);
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.description = String.format("%s %.2f from %s to %s", type, amount,
                fromAccount != null ? fromAccount : "-", toAccount != null ? toAccount : "-");
    }

    // ====================== NEW Constructor for DAO loading (from DB) ======================
    // هذا المُنشئ للاستخدام داخلي فقط عند تحميل البيانات من قاعدة البيانات
    public Transaction(String transactionId, String type, double amount, LocalDateTime timestamp,
                       String fromAccount, String toAccount, String status, String description) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.status = status;
        this.description = description;
    }

    // ====================== Setters ======================
    public void setStatus(String status) {
        this.status = status;
    }

    // ====================== Helper Methods ======================
    public boolean isApproved() {
        return "APPROVED".equals(status) || "COMPLETED".equals(status);
    }

    public boolean isPendingManagerApproval() {
        return "PENDING_MANAGER_APPROVAL".equals(status);
    }

    // ====================== Getters ======================
    public String getTransactionId() { return transactionId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }

    // ====================== toString ======================
    @Override
    public String toString() {
        return String.format("[%s] %s | %.2f | %s → %s | Status: %-25s | %s",
                timestamp.toString().substring(0, 19).replace("T", " "),
                type,
                amount,
                fromAccount != null ? fromAccount : "-",
                toAccount != null ? toAccount : "-",
                status,
                description);
    }
}