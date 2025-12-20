// src/main/java/com/bankingSystem/CustomerService/SupportTicket.java
package com.bankingSystem.CustomerService;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * كلاس يمثل تذكرة دعم/استفسار عميل
 */
public class SupportTicket {
    private String ticketId;
    private String customerId;
    private String subject;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String assignedTo; // User ID of support staff assigned
    private String resolution; // Resolution notes
    private TicketCategory category;

    public SupportTicket(String customerId, String subject, String description,
                         TicketPriority priority, TicketCategory category) {
        this.ticketId = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.subject = subject;
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.status = TicketStatus.OPEN;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.assignedTo = null;
        this.resolution = null;
    }

    // Constructor for loading from database
    public SupportTicket(String ticketId, String customerId, String subject, String description,
                         TicketStatus status, TicketPriority priority, LocalDateTime createdAt,
                         LocalDateTime updatedAt, String assignedTo, String resolution,
                         TicketCategory category) {
        this.ticketId = ticketId;
        this.customerId = customerId;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.assignedTo = assignedTo;
        this.resolution = resolution;
        this.category = category;
    }

    public void assignTo(String staffId) {
        this.assignedTo = staffId;
        this.status = TicketStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }

    public void resolve(String resolution) {
        this.resolution = resolution;
        this.status = TicketStatus.RESOLVED;
        this.updatedAt = LocalDateTime.now();
    }

    public void close() {
        this.status = TicketStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getTicketId() {
        return ticketId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getResolution() {
        return resolution;
    }

    public TicketCategory getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return String.format(
                "[%s] %s | Priority: %s | Status: %s | Category: %s | Customer: %s | Created: %s",
                ticketId.substring(0, 8),
                subject,
                priority,
                status,
                category,
                customerId,
                createdAt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
    }

    public enum TicketStatus {
        OPEN,           // مفتوحة - في انتظار المعالجة
        IN_PROGRESS,    // قيد المعالجة
        RESOLVED,       // تم الحل
        CLOSED          // مغلقة
    }

    public enum TicketPriority {
        LOW,        // منخفض
        MEDIUM,     // متوسط
        HIGH,       // عالي
        URGENT      // عاجل
    }

    public enum TicketCategory {
        ACCOUNT_ISSUE,          // مشكلة في الحساب
        TRANSACTION_ISSUE,      // مشكلة في المعاملة
        TECHNICAL_SUPPORT,      // دعم فني
        BILLING_QUESTION,       // استفسار فواتير
        GENERAL_INQUIRY         // استفسار عام
    }
}
