// src/main/java/com/bankingSystem/Roles/Manager.java
package com.bankingSystem.user;

import com.bankingSystem.Database.TransactionDAO;
import com.bankingSystem.Transaction.TransactionService;

public class Manager extends User {
    public Manager(String username, String password) {
        super(username, password, Role.MANAGER);
    }

    public Manager(String userId, String username, String passwordHash) {
        super(userId, username, passwordHash, Role.MANAGER);
    }

    // Managers can approve large transactions (example method)
    public void approveTransaction(String transactionId) {
        TransactionDAO txDAO = new TransactionDAO();
        txDAO.updateTransactionStatus(transactionId, "APPROVED_BY_MANAGER");

        // Automatically trigger execution
        new TransactionService().executeApprovedTransaction(transactionId);

        System.out.println("Manager " + username + " approved and executed transaction " + transactionId);
    }
}