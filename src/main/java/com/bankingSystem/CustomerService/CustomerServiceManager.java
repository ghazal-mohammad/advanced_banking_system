// src/main/java/com/bankingSystem/CustomerService/CustomerServiceManager.java
package com.bankingSystem.CustomerService;

import com.bankingSystem.Database.SupportTicketDAO;

import java.util.List;

/**
 * مدير خدمة العملاء: يدير نظام تذاكر الدعم باستخدام Chain of Responsibility Pattern
 * Singleton Pattern للوصول الموحد
 */
public class CustomerServiceManager {

    // ✅ Singleton Pattern
    private static CustomerServiceManager instance;

    private final SupportTicketDAO supportTicketDAO;
    private final SupportTicketHandler ticketHandlerChain;

    private CustomerServiceManager() {
        this.supportTicketDAO = new SupportTicketDAO();
        // ✅ Chain of Responsibility: بناء سلسلة المعالجات
        this.ticketHandlerChain = buildHandlerChain();
    }

    public static synchronized CustomerServiceManager getInstance() {
        if (instance == null) {
            instance = new CustomerServiceManager();
        }
        return instance;
    }

    /**
     * بناء سلسلة معالجات تذاكر الدعم (Chain of Responsibility)
     */
    private SupportTicketHandler buildHandlerChain() {
        // إنشاء المعالجات
        SupportTicketHandler firstLevel = new FirstLevelSupportHandler();
        SupportTicketHandler secondLevel = new SecondLevelSupportHandler();
        SupportTicketHandler managerLevel = new ManagerSupportHandler();

        // ربط السلسلة: First -> Second -> Manager
        firstLevel.setNext(secondLevel);
        secondLevel.setNext(managerLevel);

        return firstLevel;
    }

    /**
     * إنشاء تذكرة دعم جديدة ومعالجتها تلقائياً
     */
    public SupportTicket createTicket(String customerId, String subject, String description,
                                      SupportTicket.TicketPriority priority,
                                      SupportTicket.TicketCategory category) {
        SupportTicket ticket = new SupportTicket(customerId, subject, description, priority, category);

        // حفظ التذكرة في قاعدة البيانات
        supportTicketDAO.saveSupportTicket(ticket);

        // ✅ Chain of Responsibility: معالجة التذكرة عبر السلسلة
        ticketHandlerChain.handle(ticket);

        // حفظ التذكرة بعد المعالجة (في حالة تم التخصيص)
        supportTicketDAO.saveSupportTicket(ticket);

        System.out.println("Support ticket created and processed: " + ticket.getTicketId().substring(0, 8));
        return ticket;
    }

    /**
     * الحصول على جميع تذاكر العميل
     */
    public List<SupportTicket> getCustomerTickets(String customerId) {
        return supportTicketDAO.loadTicketsByCustomer(customerId);
    }

    /**
     * الحصول على جميع التذاكر المفتوحة
     */
    public List<SupportTicket> getOpenTickets() {
        return supportTicketDAO.loadOpenTickets();
    }

    /**
     * الحصول على جميع التذاكر
     */
    public List<SupportTicket> getAllTickets() {
        return supportTicketDAO.loadAllSupportTickets();
    }

    /**
     * حل تذكرة دعم
     */
    public void resolveTicket(String ticketId, String resolution) {
        SupportTicket ticket = supportTicketDAO.loadTicketById(ticketId);
        if (ticket != null) {
            ticket.resolve(resolution);
            supportTicketDAO.saveSupportTicket(ticket);
            System.out.println("Ticket resolved: " + ticketId.substring(0, 8));
        }
    }

    /**
     * إغلاق تذكرة دعم
     */
    public void closeTicket(String ticketId) {
        SupportTicket ticket = supportTicketDAO.loadTicketById(ticketId);
        if (ticket != null) {
            ticket.close();
            supportTicketDAO.saveSupportTicket(ticket);
            System.out.println("Ticket closed: " + ticketId.substring(0, 8));
        }
    }

    /**
     * إعادة معالجة تذكرة (في حالة الحاجة لإعادة التصعيد)
     */
    public void reprocessTicket(String ticketId) {
        SupportTicket ticket = supportTicketDAO.loadTicketById(ticketId);
        if (ticket != null) {
            ticket.setStatus(SupportTicket.TicketStatus.OPEN);
            ticketHandlerChain.handle(ticket);
            supportTicketDAO.saveSupportTicket(ticket);
            System.out.println("Ticket reprocessed: " + ticketId.substring(0, 8));
        }
    }
}
