// ManagerApprovalHandler.java
package com.bankingSystem.Transaction.ChainOfResponsability;

import com.bankingSystem.Transaction.Transaction;

public class ManagerApprovalHandler extends ApprovalHandler {
    @Override
    public void handle(Transaction transaction) {
        transaction.setStatus("PENDING_MANAGER_APPROVAL");
        System.out.println("ALERT: Transaction > 50,000 requires Manager approval!");
        // في الواقع هنا ممكن نضيف هنا Observer لإرسال إشعار للمدير
    }
}