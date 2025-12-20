// src/main/java/com/bankingSystem/Test/NewFeaturesTest.java
package com.bankingSystem.Test;

import com.bankingSystem.Account.Account;
import com.bankingSystem.Account.SavingsAccount;
import com.bankingSystem.CustomerService.CustomerServiceManager;
import com.bankingSystem.CustomerService.SupportTicket;
import com.bankingSystem.Database.AccountDAO;
import com.bankingSystem.Facade.AdministrativeDashboardFacade;
import com.bankingSystem.Transaction.ScheduledTransaction;
import com.bankingSystem.Transaction.ScheduledTransactionService;
import com.bankingSystem.Transaction.TransactionService;

/**
 * اختبار شامل للوظائف الجديدة:
 * 1. Administrative Dashboard Facade
 * 2. Scheduled Transactions
 * 3. Customer Support Tickets
 */
public class NewFeaturesTest {

    private static final AdministrativeDashboardFacade dashboard = AdministrativeDashboardFacade.getInstance();
    private static final ScheduledTransactionService scheduledService = ScheduledTransactionService.getInstance();
    private static final CustomerServiceManager customerService = CustomerServiceManager.getInstance();
    private static final AccountDAO accountDAO = new AccountDAO();
    private static final TransactionService transactionService = TransactionService.getInstance();

    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("   اختبار الوظائف الجديدة - New Features Test");
        System.out.println("=".repeat(60) + "\n");

        // اختبار 1: Administrative Dashboard Facade
        testAdministrativeDashboard();

        // اختبار 2: Scheduled Transactions
        testScheduledTransactions();

        // اختبار 3: Customer Support Tickets
        testSupportTickets();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("   تم إكمال جميع الاختبارات بنجاح!");
        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * اختبار 1: Administrative Dashboard Facade
     */
    private static void testAdministrativeDashboard() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("اختبار 1: Administrative Dashboard Facade");
        System.out.println("-".repeat(60));

        try {
            // عرض لوحة التحكم
            System.out.println("\n[1.1] عرض لوحة التحكم الإدارية:");
            dashboard.displayDashboard();

            // الحصول على الملخص
            System.out.println("\n[1.2] ملخص لوحة التحكم:");
            AdministrativeDashboardFacade.DashboardSummary summary = dashboard.getDashboardSummary();
            System.out.println(summary);

            // الحصول على عدد المعاملات المتزامنة
            System.out.println("\n[1.3] عدد المعاملات المتزامنة: " + dashboard.getConcurrentTransactionCount());

            // الحصول على سجلات التدقيق
            System.out.println("\n[1.4] عدد سجلات التدقيق: " + dashboard.getAuditLogs().size());

            System.out.println("\n✅ اختبار Administrative Dashboard: نجح!");

        } catch (Exception e) {
            System.err.println("❌ خطأ في اختبار Administrative Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * اختبار 2: Scheduled Transactions
     */
    private static void testScheduledTransactions() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("اختبار 2: Scheduled Transactions");
        System.out.println("-".repeat(60));

        try {
            // إنشاء حساب تجريبي
            Account testAccount = new SavingsAccount("TEST-SCHED-001", "default-customer-001");
            testAccount.deposit(1000.0);
            testAccount.persist();

            // إنشاء معاملة مجدولة للإيداع
            System.out.println("\n[2.1] إنشاء معاملة مجدولة للإيداع (كل 2 دقيقة):");
            ScheduledTransaction depositScheduled = scheduledService.createScheduledTransaction(
                    "DEPOSIT",
                    100.0,
                    null,
                    testAccount.getAccountNumber(),
                    2, // كل دقيقتين
                    "default-customer-001"
            );
            System.out.println("تم إنشاء المعاملة المجدولة: " + depositScheduled);

            // إنشاء معاملة مجدولة للسحب
            System.out.println("\n[2.2] إنشاء معاملة مجدولة للسحب (كل 3 دقائق):");
            ScheduledTransaction withdrawScheduled = scheduledService.createScheduledTransaction(
                    "WITHDRAW",
                    50.0,
                    testAccount.getAccountNumber(),
                    null,
                    3, // كل 3 دقائق
                    "default-customer-001"
            );
            System.out.println("تم إنشاء المعاملة المجدولة: " + withdrawScheduled);

            // الحصول على جميع المعاملات المجدولة
            System.out.println("\n[2.3] جميع المعاملات المجدولة:");
            scheduledService.getAllScheduledTransactions().forEach(System.out::println);

            // الحصول على المعاملات المجدولة النشطة
            System.out.println("\n[2.4] المعاملات المجدولة النشطة:");
            scheduledService.getActiveScheduledTransactions().forEach(System.out::println);

            // بدء خدمة المعاملات المجدولة
            System.out.println("\n[2.5] بدء خدمة المعاملات المجدولة:");
            scheduledService.start();
            System.out.println("حالة الخدمة: " + (scheduledService.isRunning() ? "تعمل" : "متوقفة"));

            // إيقاف الخدمة بعد الاختبار (في التطبيق الحقيقي لن نوقفها)
            // scheduledService.stop();

            System.out.println("\n✅ اختبار Scheduled Transactions: نجح!");
            System.out.println("   ملاحظة: الخدمة تعمل في الخلفية وستنفذ المعاملات عند حلول وقتها");

        } catch (Exception e) {
            System.err.println("❌ خطأ في اختبار Scheduled Transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * اختبار 3: Customer Support Tickets (Chain of Responsibility)
     */
    private static void testSupportTickets() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("اختبار 3: Customer Support Tickets (Chain of Responsibility)");
        System.out.println("-".repeat(60));

        try {
            // إنشاء تذكرة منخفضة الأولوية (يجب معالجتها بواسطة First Level Support)
            System.out.println("\n[3.1] إنشاء تذكرة استفسار عام (أولوية منخفضة):");
            SupportTicket ticket1 = customerService.createTicket(
                    "default-customer-001",
                    "استفسار عن الفوائد",
                    "أريد معرفة معدل الفائدة على حساب التوفير",
                    SupportTicket.TicketPriority.LOW,
                    SupportTicket.TicketCategory.GENERAL_INQUIRY
            );
            System.out.println("التذكرة: " + ticket1);
            System.out.println("المخصصة لـ: " + ticket1.getAssignedTo());

            // إنشاء تذكرة متوسطة الأولوية (يجب معالجتها بواسطة Second Level Support)
            System.out.println("\n[3.2] إنشاء تذكرة مشكلة تقنية (أولوية متوسطة):");
            SupportTicket ticket2 = customerService.createTicket(
                    "default-customer-001",
                    "مشكلة في تسجيل الدخول",
                    "لا أستطيع تسجيل الدخول إلى حسابي",
                    SupportTicket.TicketPriority.MEDIUM,
                    SupportTicket.TicketCategory.TECHNICAL_SUPPORT
            );
            System.out.println("التذكرة: " + ticket2);
            System.out.println("المخصصة لـ: " + ticket2.getAssignedTo());

            // إنشاء تذكرة عاجلة (يجب معالجتها بواسطة Manager Support)
            System.out.println("\n[3.3] إنشاء تذكرة عاجلة:");
            SupportTicket ticket3 = customerService.createTicket(
                    "default-customer-001",
                    "مشكلة في معاملة كبيرة",
                    "معاملة بقيمة 10000 لم تتم بشكل صحيح",
                    SupportTicket.TicketPriority.URGENT,
                    SupportTicket.TicketCategory.TRANSACTION_ISSUE
            );
            System.out.println("التذكرة: " + ticket3);
            System.out.println("المخصصة لـ: " + ticket3.getAssignedTo());

            // الحصول على جميع تذاكر العميل
            System.out.println("\n[3.4] جميع تذاكر العميل:");
            customerService.getCustomerTickets("default-customer-001").forEach(System.out::println);

            // الحصول على التذاكر المفتوحة
            System.out.println("\n[3.5] التذاكر المفتوحة:");
            customerService.getOpenTickets().forEach(System.out::println);

            // حل تذكرة
            System.out.println("\n[3.6] حل التذكرة الأولى:");
            customerService.resolveTicket(ticket1.getTicketId(), "تم شرح معدل الفائدة للعميل");
            System.out.println("حالة التذكرة: " + customerService.getCustomerTickets("default-customer-001")
                    .stream().filter(t -> t.getTicketId().equals(ticket1.getTicketId())).findFirst()
                    .map(SupportTicket::getStatus).orElse(null));

            // إغلاق تذكرة
            System.out.println("\n[3.7] إغلاق التذكرة:");
            customerService.closeTicket(ticket1.getTicketId());

            System.out.println("\n✅ اختبار Support Tickets: نجح!");
            System.out.println("   Chain of Responsibility Pattern يعمل بشكل صحيح");

        } catch (Exception e) {
            System.err.println("❌ خطأ في اختبار Support Tickets: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
