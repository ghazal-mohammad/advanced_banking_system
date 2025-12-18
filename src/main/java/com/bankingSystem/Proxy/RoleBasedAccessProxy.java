// src/main/java/com/bankingSystem/Service/RoleBasedAccessProxy.java
package com.bankingSystem.Proxy;

import com.bankingSystem.Account.Account;
import com.bankingSystem.user.*;
import com.bankingSystem.Transaction.Transaction;

import java.util.List;

public class RoleBasedAccessProxy implements BankingService {
    private final BankingServiceImpl realService = new BankingServiceImpl();
    private final User currentUser; // Set at login

    public RoleBasedAccessProxy(User currentUser) {
        this.currentUser = currentUser;
    }

    private void checkPermission(String requiredRole) {
        if (!currentUser.getRole().name().equalsIgnoreCase(requiredRole) &&
                !currentUser.getRole().name().equalsIgnoreCase("ADMIN")) { // Admin can do everything
            throw new SecurityException("Access denied: Required role " + requiredRole);
        }
    }

    // Customer methods
    @Override
    public void performTransaction(String ownerId, Account from, Account to, double amount, String type) {
        if (currentUser.getRole() == Role.CUSTOMER) {
            if (!currentUser.getUserId().equals(ownerId)) {
                throw new SecurityException("Customers can only transact on their own accounts");
            }
        }
        realService.performTransaction(ownerId, from, to, amount, type);
    }

    @Override
    public List<Transaction> getTransactionHistory(String ownerId, String accountNumber) {
        if (currentUser.getRole() == Role.CUSTOMER && !currentUser.getUserId().equals(ownerId)) {
            throw new SecurityException("Customers can only view their own history");
        }
        return realService.getTransactionHistory(ownerId, accountNumber);
    }

    @Override
    public void modifyAccountInfo(String ownerId, Account account, String field, String newValue) {
        if (currentUser.getRole() == Role.CUSTOMER && !currentUser.getUserId().equals(ownerId)) {
            throw new SecurityException("Customers can only modify their own accounts");
        }
        realService.modifyAccountInfo(ownerId, account, field, newValue);
    }

    // Teller methods
    @Override
    public Account createAccount(String accountType, String ownerId) {
        checkPermission("TELLER");
        return realService.createAccount(accountType, ownerId);
    }

    @Override
    public void changeAccountState(String accountNumber, String newState) {
        checkPermission("TELLER");
        realService.changeAccountState(accountNumber, newState);
    }

    @Override
    public Account findAccountByNumber(String accountNumber) {
        checkPermission("TELLER");
        return realService.findAccountByNumber(accountNumber);
    }

    // Manager methods
    @Override
    public List<User> getAllTellers() {
        checkPermission("MANAGER");
        return realService.getAllTellers();
    }

    @Override
    public void addTeller(String username, String password) {
        checkPermission("MANAGER");
        realService.addTeller(username, password);
    }

    @Override
    public void deleteTeller(String userId) {
        checkPermission("MANAGER");
        realService.deleteTeller(userId);
    }

    @Override
    public List<User> getAllCustomers() {
        checkPermission("MANAGER");
        return realService.getAllCustomers();
    }

    @Override
    public List<Transaction> getAllTransactionsReport() {
        checkPermission("MANAGER");
        return realService.getAllTransactionsReport();
    }

    @Override
    public List<Account> getAllAccountsReport() {
        checkPermission("MANAGER");
        return realService.getAllAccountsReport();
    }
}