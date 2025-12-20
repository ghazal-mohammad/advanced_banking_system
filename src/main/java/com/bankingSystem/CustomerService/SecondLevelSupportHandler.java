// src/main/java/com/bankingSystem/CustomerService/SecondLevelSupportHandler.java
package com.bankingSystem.CustomerService;

/**
 * معالج دعم المستوى الثاني: يتعامل مع المشاكل التقنية ومشاكل الحسابات
 * Chain of Responsibility Pattern
 */
public class SecondLevelSupportHandler extends SupportTicketHandler {

    @Override
    protected boolean canHandle(SupportTicket ticket) {
        // يمكن معالجة المشاكل التقنية ومشاكل الحسابات
        return (ticket.getCategory() == SupportTicket.TicketCategory.TECHNICAL_SUPPORT ||
                ticket.getCategory() == SupportTicket.TicketCategory.ACCOUNT_ISSUE ||
                ticket.getCategory() == SupportTicket.TicketCategory.TRANSACTION_ISSUE) &&
                (ticket.getPriority() == SupportTicket.TicketPriority.MEDIUM ||
                        ticket.getPriority() == SupportTicket.TicketPriority.HIGH);
    }

    @Override
    protected void processTicket(SupportTicket ticket) {
        System.out.println("Second Level Support handling ticket: " + ticket.getTicketId().substring(0, 8));
        ticket.assignTo("second-level-support");
        // معالجة متقدمة - يمكن إضافة منطق إضافي هنا
        System.out.println("Ticket assigned to second level support specialist");
    }

    @Override
    public String getHandlerName() {
        return "Second Level Support";
    }
}
