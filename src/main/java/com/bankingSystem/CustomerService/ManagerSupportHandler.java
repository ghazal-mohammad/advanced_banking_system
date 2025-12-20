// src/main/java/com/bankingSystem/CustomerService/ManagerSupportHandler.java
package com.bankingSystem.CustomerService;

/**
 * معالج دعم المدير: يتعامل مع الحالات العاجلة والمشاكل المعقدة
 * Chain of Responsibility Pattern
 */
public class ManagerSupportHandler extends SupportTicketHandler {

    @Override
    protected boolean canHandle(SupportTicket ticket) {
        // يمكن معالجة جميع التذاكر عالية الأولوية أو التي فشلت المعالجات الأخرى
        return ticket.getPriority() == SupportTicket.TicketPriority.URGENT ||
                ticket.getStatus() == SupportTicket.TicketStatus.IN_PROGRESS;
    }

    @Override
    protected void processTicket(SupportTicket ticket) {
        System.out.println("Manager Support handling ticket: " + ticket.getTicketId().substring(0, 8));
        ticket.assignTo("manager-support");
        // معالجة إدارية - يمكن إضافة منطق إضافي هنا
        System.out.println("Ticket escalated to manager for review");
    }

    @Override
    public String getHandlerName() {
        return "Manager Support";
    }
}
