// src/main/java/com/bankingSystem/BankingConsoleApp.java
package com.bankingSystem;

import com.bankingSystem.Account.*;
import com.bankingSystem.Database.*;
import com.bankingSystem.Transaction.TransactionService;
import com.bankingSystem.user.*;
import com.bankingSystem.Proxy.BankingService;
import com.bankingSystem.Proxy.RoleBasedAccessProxy;
import com.bankingSystem.Transaction.Transaction;

import java.util.List;
import java.util.Scanner;

public class CoreApplication {

    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDAO userDAO = new UserDAO();
    private static final AccountDAO accountDAO = new AccountDAO();
    private static final TransactionDAO transactionDAO = new TransactionDAO();

    public static void main(String[] args) {
        System.out.println("=== Welcome to Advanced Banking System ===\n");

        while (true) {
            System.out.println("Choose your role:");
            System.out.println("1. Customer");
            System.out.println("2. Teller");
            System.out.println("3. Manager");
            System.out.println("4. Exit");
            System.out.print("Enter choice (1-4): ");

            String roleChoice = scanner.nextLine().trim();

            if (roleChoice.equals("4")) {
                System.out.println("Thank you for using the banking system. Goodbye!");
                break;
            }

            Role selectedRole = switch (roleChoice) {
                case "1" -> Role.CUSTOMER;
                case "2" -> Role.TELLER;
                case "3" -> Role.MANAGER;
                default -> {
                    System.out.println("Invalid choice. Try again.\n");
                    yield null;
                }
            };

            if (selectedRole == null) continue;

            // Login
            System.out.print("Enter your username: ");
            String username = scanner.nextLine().trim();

            User user = userDAO.loadUser(username);
            if (user == null || user.getRole() != selectedRole) {
                System.out.println("Login failed: User not found or wrong role.\n");
                continue;
            }

            System.out.println("Login successful! Welcome, " + user.getUsername() + " (" + user.getRole().getDisplayName() + ")\n");

            // Create proxy for this user
            BankingService bankingService = new RoleBasedAccessProxy(user);

            // Start role-specific menu loop
            roleMenuLoop(bankingService, user);
        }
    }

    private static void roleMenuLoop(BankingService service, User currentUser) {
        while (true) {
            System.out.println("=== " + currentUser.getRole().getDisplayName() + " Menu ===");
            switch (currentUser.getRole()) {
                case CUSTOMER -> customerMenu();
                case TELLER -> tellerMenu();
                case MANAGER -> managerMenu();
            }

            System.out.print("Enter your choice (or 'logout' to log out): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("logout")) {
                System.out.println("Logged out successfully.\n");
                break;
            }

            try {
                switch (currentUser.getRole()) {
                    case CUSTOMER -> handleCustomerAction(service, currentUser, choice);
                    case TELLER -> handleTellerAction(service, choice);
                    case MANAGER -> handleManagerAction(service, choice);
                }
            } catch (SecurityException e) {
                System.out.println("Access Denied: " + e.getMessage() + "\n");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + "\n");
            }
        }
    }

    private static void customerMenu() {
        System.out.println("1. View my transaction history");
        System.out.println("2. Deposit money");
        System.out.println("3. Withdraw money");
        System.out.println("4. Transfer money to another my account");
        System.out.println("5. View my accounts");
    }

    private static void tellerMenu() {
        System.out.println("1. Create new account");
        System.out.println("2. Find account by number");
        System.out.println("3. Change account state (freeze/close/etc)");
        System.out.println("4. Perform transaction on any account");
    }

    private static void managerMenu() {
        System.out.println("1. View all customers");
        System.out.println("2. View all tellers");
        System.out.println("3. Add new teller");
        System.out.println("4. Delete teller");
        System.out.println("5. View all transactions report");
        System.out.println("6. View all accounts report");
    }

    // ====================== CUSTOMER ACTIONS ======================
    private static void handleCustomerAction(BankingService service, User user, String choice) {
        List<Account> myAccounts = accountDAO.getAccountsByOwner(user.getUserId());

        if (myAccounts.isEmpty()) {
            System.out.println("You have no accounts yet. Ask a teller to create one.\n");
            return;
        }

        switch (choice) {
            case "1" -> {
                System.out.print("Enter account number: ");
                String accNum = scanner.nextLine();
                List<Transaction> history = service.getTransactionHistory(user.getUserId(), accNum);
                System.out.println("\n--- Transaction History ---");
                history.forEach(System.out::println);
                System.out.println();
            }
            case "2", "3", "4" -> performTransaction(service, user, myAccounts, choice);
            case "5" -> {
                System.out.println("\n--- Your Accounts ---");
                myAccounts.forEach(acc -> acc.showDetails());
                System.out.println();
            }
            default -> System.out.println("Invalid choice.\n");
        }
    }

    private static void performTransaction(BankingService service, User user, List<Account> accounts, String typeChoice) {
        System.out.println("Your accounts:");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println((i+1) + ". " + accounts.get(i));
        }

        System.out.print("From account number (or leave empty for deposit): ");
        String fromNum = scanner.nextLine().trim();
        Account from = fromNum.isEmpty() ? null : accountDAO.loadAccount(fromNum);

        System.out.print("To account number (or leave empty for withdrawal): ");
        String toNum = scanner.nextLine().trim();
        Account to = toNum.isEmpty() ? null : accountDAO.loadAccount(toNum);

        System.out.print("Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());

        String type = switch (typeChoice) {
            case "2" -> "DEPOSIT";
            case "3" -> "WITHDRAW";
            case "4" -> "TRANSFER";
            default -> "";
        };

        service.performTransaction(user.getUserId(), from, to, amount, type);
        System.out.println();
    }

    // ====================== TELLER ACTIONS ======================
    private static void handleTellerAction(BankingService service, String choice) {
        switch (choice) {
            case "1" -> {
                System.out.print("Account type (savings/checking/loan/investment): ");
                String type = scanner.nextLine();
                System.out.print("Owner username: ");
                String ownerUsername = scanner.nextLine();
                User owner = userDAO.loadUser(ownerUsername);
                if (owner == null) {
                    System.out.println("Owner not found.\n");
                    return;
                }
                Account acc = service.createAccount(type, owner.getUserId());
                System.out.println("Account created: " + acc.getAccountNumber() + "\n");
            }
            case "2" -> {
                System.out.print("Account number: ");
                String num = scanner.nextLine();
                Account acc = service.findAccountByNumber(num);
                if (acc != null) acc.showDetails();
                else System.out.println("Account not found.\n");
            }
            case "3" -> {
                System.out.print("Account number: ");
                String num = scanner.nextLine();
                System.out.print("New state (active/frozen/suspended/closed): ");
                String state = scanner.nextLine();
                service.changeAccountState(num, state);
                System.out.println("State updated.\n");
            }
            case "4" -> {
                // Teller can do transaction on any account
                System.out.print("From account number: ");
                String fromNum = scanner.nextLine();
                Account from = accountDAO.loadAccount(fromNum);
                System.out.print("To account number (empty for deposit/withdraw): ");
                String toNum = scanner.nextLine();
                Account to = toNum.isEmpty() ? null : accountDAO.loadAccount(toNum);
                System.out.print("Amount: ");
                double amount = Double.parseDouble(scanner.nextLine());
                System.out.print("Type (deposit/withdraw/transfer): ");
                String type = scanner.nextLine().toUpperCase();
                new TransactionService().processTransaction(from, to, amount, type); // Direct access for teller
                System.out.println();
            }
            default -> System.out.println("Invalid choice.\n");
        }
    }

    // ====================== MANAGER ACTIONS ======================
    private static void handleManagerAction(BankingService service, String choice) {
        switch (choice) {
            case "1" -> {
                List<User> customers = service.getAllCustomers();
                System.out.println("\n--- All Customers ---");
                customers.forEach(System.out::println);
                System.out.println();
            }
            case "2" -> {
                List<User> tellers = service.getAllTellers();
                System.out.println("\n--- All Tellers ---");
                tellers.forEach(System.out::println);
                System.out.println();
            }
            case "3" -> {
                System.out.print("New teller username: ");
                String username = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                service.addTeller(username, password);
                System.out.println("Teller added.\n");
            }
            case "4" -> {
                System.out.print("Teller user ID to delete: ");
                String id = scanner.nextLine();
                service.deleteTeller(id);
                System.out.println("Teller deleted (if existed).\n");
            }
            case "5" -> {
                List<Transaction> allTx = service.getAllTransactionsReport();
                System.out.println("\n--- All Transactions Report (" + allTx.size() + ") ---");
                allTx.forEach(System.out::println);
                System.out.println();
            }
            case "6" -> {
                List<Account> allAcc = service.getAllAccountsReport();
                System.out.println("\n--- All Accounts Report (" + allAcc.size() + ") ---");
                allAcc.forEach(acc -> acc.showDetails());
                System.out.println();
            }
            default -> System.out.println("Invalid choice.\n");
        }
    }
}





//package com.bankingSystem;
//
//import com.bankingSystem.Account.*;
//import com.bankingSystem.Account.CompositePattern.AccountGroup;
//import com.bankingSystem.Database.*;
//import com.bankingSystem.Transaction.Transaction;
//import com.bankingSystem.user.*;
//import com.bankingSystem.Transaction.TransactionService;
//
//import java.util.List;
//
//public class CoreApplication {
//
//    private static TransactionService transactionService = new TransactionService();
//    private static AccountDAO accountDAO = new AccountDAO();
//    private static UserDAO userDAO = new UserDAO();
//    private static TransactionDAO transactionDAO = new TransactionDAO();
//
//    public static void main(String[] args) {
//        System.out.println("=== Advanced Banking System Demo ===\n");
//        System.out.println("Phase 1: Creating Users (Customer, Teller, Manager)\n");
//
//        Customer customer = new Customer("ahmed.ali", "pass123");
//        Teller teller = new Teller("teller_sara", "tellerpass");
//        Manager manager = new Manager("mgr_khaled", "mgrpass");
//
//        customer.persist();
//        teller.persist();
//        manager.persist();
//
//        System.out.println("Users created and saved:\n" + customer + "\n" + teller + "\n" + manager + "\n");
//
//        // ===================================================================
//        System.out.println("Phase 2: Creating Accounts for Ahmed\n");
//
//        SavingsAccount savings = new SavingsAccount("SAV-1001", customer.getUserId());
//        CheckingAccount checking = new CheckingAccount("CHK-2001", customer.getUserId());
//        LoanAccount loan = new LoanAccount("LOAN-3001", customer.getUserId(), 50000.0);
//        InvestmentAccount investment = new InvestmentAccount("INV-4001", customer.getUserId());
//
//        savings.persist();
//        checking.persist();
//        loan.persist();
//        investment.persist();
//
//        // Composite Pattern: Family Group Account
//        AccountGroup familyGroup = new AccountGroup("Ahmed's Family Accounts");
//        familyGroup.add(savings);
//        familyGroup.add(checking);
//        familyGroup.add(investment);
//
//        System.out.println("Accounts created and grouped:");
//        familyGroup.showDetails();
//        System.out.println();
//
//        // ===================================================================
//        System.out.println("Phase 3: Normal Operations (Deposits & Small Transfers)\n");
//
//        transactionService.processTransaction(null, savings, 15000.0, "DEPOSIT");     // Deposit 15k
//        transactionService.processTransaction(null, savings, 15000.0, "DEPOSIT");     // Deposit 15k
//        transactionService.processTransaction(null, savings, 15000.0, "DEPOSIT");     // Deposit 15k
//        transactionService.processTransaction(null, savings, 15000.0, "DEPOSIT");     // Deposit 15k
//        transactionService.processTransaction(null, savings, 15000.0, "DEPOSIT");     // Deposit 15k
//        transactionService.processTransaction(null, savings, 15000.0, "DEPOSIT");     // Deposit 15k
//        transactionService.processTransaction(null, checking, 8000.0, "DEPOSIT");     // Deposit 8k
//        transactionService.processTransaction(checking, savings, 3000.0, "TRANSFER");       // Small transfer
//        System.out.println("\nSavings after operations:");
//        savings.showDetails();
//        System.out.println("Checking after operations:");
//        checking.showDetails();
//
//        // ===================================================================
//        System.out.println("\nPhase 4: Large Transfer (>50,000) → Requires Manager Approval\n");
//
//        transactionService.processTransaction(savings, checking, 60000.0, "TRANSFER");
//        // This will be PENDING - money NOT moved yet
//
//        System.out.println("\nCurrent balances (money not moved yet):");
//        savings.showDetails();
//        checking.showDetails();
//
//        // ===================================================================
//        System.out.println("\nPhase 5: Manager Approves Large Transaction\n");
//
//        User loadedManager = userDAO.loadUser("mgr_khaled");
//        if (loadedManager instanceof Manager mgr) {
//            List<Transaction> pending = transactionDAO.loadPendingTransactions();
//            if (!pending.isEmpty()) {
//                Transaction pendingTx = pending.get(pending.size() - 1); // Latest one
//                System.out.println("Found pending transaction:");
//                System.out.println("  → " + pendingTx);
//
//                mgr.approveTransaction(pendingTx.getTransactionId());
//
//                System.out.println("\nFinal balances after approval:");
//                reloadAndShowAccount(savings);
//                reloadAndShowAccount(checking);
//            } else {
//                System.out.println("No pending transactions found (did you run a large transfer?)");
//            }
//        }
//        // ===================================================================
//        System.out.println("\nPhase 6: State Pattern Demo (Freeze & Close Account)\n");
//
//        System.out.println("Freezing Savings Account...");
//        savings.freeze();
//        savings.persist();
//
//        try {
//            transactionService.processTransaction(null, savings, 1000.0, "DEPOSIT"); // Allowed in Frozen
//            transactionService.processTransaction(savings, checking, 500.0, "TRANSFER"); // Blocked
//        } catch (Exception e) {
//            System.out.println("Expected error: " + e.getMessage());
//        }
//
//        System.out.println("\nClosing Loan Account...");
//        loan.close();
//        loan.persist();
//
//        try {
//            loan.makePayment(1000);
//        } catch (Exception e) {
//            System.out.println("Expected error on closed account: " + e.getMessage());
//        }
//
//        // ===================================================================
//        System.out.println("\nPhase 7: Interest Calculation (Strategy Pattern)\n");
//
//        System.out.printf("Savings Interest (4%%): %.2f%n", savings.calculateInterest());
//        System.out.printf("Loan Interest (monthly): %.2f%n", loan.calculateInterest());
//        System.out.printf("Investment Interest (volatile): %.2f%n", investment.calculateInterest());
//
//        // ===================================================================
//        System.out.println("\nPhase 8: Simulate Application Restart – Reload from DB\n");
//
//        System.out.println("Reloading all accounts for Ahmed from database...");
//        List<Account> reloadedAccounts = accountDAO.getAccountsByOwner(customer.getUserId());
//
//        for (Account acc : reloadedAccounts) {
//            acc.showDetails();
//            System.out.println("Transaction History (" + acc.getTransactionHistory().size() + " entries):");
//            for (Transaction tx : acc.getTransactionHistory()) {
//                System.out.println("  → " + tx);
//            }
//            System.out.println();
//        }
//
//        System.out.println("\n=== Demo Completed Successfully! ===");
//        System.out.println("You have demonstrated:");
//        System.out.println("   • Composite, State, Strategy, Chain of Responsibility");
//        System.out.println("   • Full persistence (H2 DB)");
//        System.out.println("   • Role-based users & manager approval workflow");
//        System.out.println("   • Transaction history & audit trail");
//        System.out.println("   • Account states and restrictions");
//    }
//
//    private static void reloadAndShowAccount(Account account) {
//        Account fresh = accountDAO.loadAccount(account.getAccountNumber());
//        if (fresh != null) {
//            fresh.showDetails();
//        }
//    }
//}