// src/main/java/com/bankingSystem/CustomerService/FirstLevelSupportHandler.java
package com.bankingSystem.CustomerService;

/**
 * معالج دعم المستوى الأول: يتعامل مع الاستفسارات العامة والمشاكل البسيطة
 * Chain of Responsibility Pattern
 */
public class FirstLevelSupportHandler extends SupportTicketHandler {

    @Override
    protected boolean canHandle(SupportTicket ticket) {
        // يمكن معالجة الاستفسارات العامة والمشاكل منخفضة الأولوية
        return (ticket.getCategory() == SupportTicket.TicketCategory.GENERAL_INQUIRY ||
                ticket.getCategory() == SupportTicket.TicketCategory.BILLING_QUESTION) &&
                (ticket.getPriority() == SupportTicket.TicketPriority.LOW ||
                        ticket.getPriority() == SupportTicket.TicketPriority.MEDIUM);
    }

    @Override
    protected void processTicket(SupportTicket ticket) {
        System.out.println("First Level Support handling ticket: " + ticket.getTicketId().substring(0, 8));
        ticket.assignTo("first-level-support");
        // معالجة أولية - يمكن إضافة منطق إضافي هنا
        System.out.println("Ticket assigned to first level support staff");
    }

    @Override
    public String getHandlerName() {
        return "First Level Support";
    }
}
