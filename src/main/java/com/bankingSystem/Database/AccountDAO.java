package com.bankingSystem.Database;

import com.bankingSystem.Account.*;
import com.bankingSystem.Account.statePattern.ActiveState;
import com.bankingSystem.Account.statePattern.ClosedState;
import com.bankingSystem.Account.statePattern.FrozenState;
import com.bankingSystem.Account.statePattern.SuspendedState;
import com.bankingSystem.Transaction.Transaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private final Connection conn = DatabaseConnection.getInstance().getConnection();


    public void saveAccount(Account account) {
        String sql = """
                MERGE INTO Accounts KEY(accountId) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account.accountId);
            pstmt.setString(2, account.getAccountNumber());
            pstmt.setDouble(3, account.getBalance());
            pstmt.setObject(4, account.creationDate);
            pstmt.setString(5, account.ownerId);
            pstmt.setString(6, account.state.getClass().getSimpleName().replace("State", ""));
            pstmt.setString(7, account.getClass().getSimpleName());

//            if (account instanceof InvestmentAccount inv) {
//                pstmt.setString(8, inv.getRiskLevel());
//            } else {
//                pstmt.setString(8, null);
//            }

            if (account instanceof LoanAccount loan) {
                pstmt.setDouble(8, loan.loanAmount); // افتح المتغير loanAmount بجعله protected أو أضف getter
            } else {
                pstmt.setDouble(8, 0.0);
            }

            pstmt.executeUpdate();
            System.out.println("Account saved/updated in database: " + account.getAccountNumber());
        } catch (SQLException e) {
            System.err.println("Error saving account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE Accounts SET balance = ? WHERE accountNumber = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountNumber);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Balance updated for " + accountNumber + " → " + newBalance);
            }
        } catch (SQLException e) {
            System.err.println("Error updating balance: " + e.getMessage());
        }
    }


    public void deleteAccount(String accountNumber) {
        String sql = "DELETE FROM Accounts WHERE accountNumber = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Account deleted permanently: " + accountNumber);
            } else {
                System.out.println("Account not found for deletion: " + accountNumber);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting account: " + e.getMessage());
        }
    }

    public void updateAccountState(String accountNumber, String newState) {
        String sql = "UPDATE Accounts SET state = ? WHERE accountNumber = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newState); // مثال: "Active", "Frozen", "Suspended", "Closed"
            pstmt.setString(2, accountNumber);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("State updated to " + newState + " for " + accountNumber);
            }
        } catch (SQLException e) {
            System.err.println("Error updating state: " + e.getMessage());
        }
    }

    public Account loadAccount(String accountNumber) {
        String sql = "SELECT * FROM Accounts WHERE accountNumber = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return buildAccountFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM Accounts";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                accounts.add(buildAccountFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    /**
     * جلب كل حسابات عميل معين (مفيد لعرض لوحة التحكم الشخصية)
     */
    public List<Account> getAccountsByOwner(String ownerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM Accounts WHERE ownerId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ownerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                accounts.add(buildAccountFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    // دالة مساعدة لتحويل الـ ResultSet إلى كائن Account
    private Account buildAccountFromResultSet(ResultSet rs) throws SQLException {
        String type = rs.getString("type");
        String number = rs.getString("accountNumber");
        String owner = rs.getString("ownerId");
        Account account;

        switch (type) {
            case "SavingsAccount" -> account = new SavingsAccount(number, owner);
            case "CheckingAccount" -> account = new CheckingAccount(number, owner);
            case "LoanAccount" -> {
                double loanAmount = rs.getDouble("loanAmount");
                account = new LoanAccount(number, owner, loanAmount);
            }
            case "InvestmentAccount" -> {

                account = new InvestmentAccount(number, owner);
//                String risk = rs.getString("riskLevel");
//                account = new InvestmentAccount(number, owner, risk);
            }
            default -> throw new IllegalArgumentException("Unknown account type: " + type);
        }

        account.accountId = rs.getString("accountId");
        account.balance = rs.getDouble("balance");
        account.creationDate = rs.getObject("creationDate", LocalDateTime.class);

        // استعادة الحالة
        String stateName = rs.getString("state");
        account.setState(switch (stateName) {
            case "Active" -> new ActiveState();
            case "Frozen" -> new FrozenState();
            case "Suspended" -> new SuspendedState();
            case "Closed" -> new ClosedState();
            default -> new ActiveState();
        });
        List<Transaction> history = new TransactionDAO().loadTransactions(account.getAccountNumber());
        account.transactionHistory.addAll(history);
        return account;
    }
}