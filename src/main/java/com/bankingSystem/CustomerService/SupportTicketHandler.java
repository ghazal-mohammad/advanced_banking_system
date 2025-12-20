// src/main/java/com/bankingSystem/CustomerService/SupportTicketHandler.java
package com.bankingSystem.CustomerService;

/**
 * Chain of Responsibility Pattern: معالج أساسي لتذاكر الدعم
 * كل معالج في السلسلة يقرر ما إذا كان يمكنه معالجة التذكرة أو يمررها للمعالج التالي
 */
public abstract class SupportTicketHandler {
    protected SupportTicketHandler nextHandler;

    public void setNext(SupportTicketHandler handler) {
        this.nextHandler = handler;
    }

    /**
     * معالجة التذكرة - كل معالج يقرر ما إذا كان يمكنه المعالجة أو يمررها للتالي
     */
    public void handle(SupportTicket ticket) {
        if (canHandle(ticket)) {
            processTicket(ticket);
        } else if (nextHandler != null) {
            nextHandler.handle(ticket);
        } else {
            // لا يوجد معالج يمكنه التعامل مع التذكرة
            System.out.println("No handler available for ticket: " + ticket.getTicketId());
        }
    }

    /**
     * التحقق مما إذا كان هذا المعالج يمكنه التعامل مع التذكرة
     */
    protected abstract boolean canHandle(SupportTicket ticket);

    /**
     * معالجة التذكرة
     */
    protected abstract void processTicket(SupportTicket ticket);

    /**
     * الحصول على اسم المعالج (للتوثيق)
     */
    public abstract String getHandlerName();
}
