package com.bankingSystem.Database;

import com.bankingSystem.Transaction.Transaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private final Connection conn = DatabaseConnection.getInstance().getConnection();


    public void saveTransaction(Transaction tx) {
        String sql = """
            INSERT INTO Transactions 
            (transactionId, type, amount, timestamp, fromAccount, toAccount, status, description) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tx.getTransactionId());
            pstmt.setString(2, tx.getType());
            pstmt.setDouble(3, tx.getAmount());
            pstmt.setObject(4, tx.getTimestamp());
            pstmt.setString(5, tx.getFromAccount());
            pstmt.setString(6, tx.getToAccount());
            pstmt.setString(7, tx.getStatus());
            pstmt.setString(8, tx.getDescription());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public List<Transaction> loadTransactions(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = """
            SELECT * FROM Transactions 
            WHERE fromAccount = ? OR toAccount = ?
            ORDER BY timestamp DESC
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, accountNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction tx = buildTransactionFromResultSet(rs);
                    transactions.add(tx);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading transactions for account: " + accountNumber);
            e.printStackTrace();
        }

        return transactions;
    }

    public List<Transaction> loadAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions ORDER BY timestamp DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transactions.add(buildTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error loading all transactions");
            e.printStackTrace();
        }

        return transactions;
    }

    public List<Transaction> loadPendingTransactions() {
        List<Transaction> pending = new ArrayList<>();
        String sql = "SELECT * FROM Transactions WHERE status = 'PENDING_MANAGER_APPROVAL'";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pending.add(buildTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pending;
    }

    public void updateTransactionStatus(String transactionId, String newStatus) {
        String sql = "UPDATE Transactions SET status = ? WHERE transactionId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, transactionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating transaction status: " + e.getMessage());
        }
    }
    // دالة مساعدة لبناء كائن Transaction من ResultSet
    private Transaction buildTransactionFromResultSet(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getString("transactionId"),
                rs.getString("type"),
                rs.getDouble("amount"),
                rs.getObject("timestamp", LocalDateTime.class),
                rs.getString("fromAccount"),
                rs.getString("toAccount"),
                rs.getString("status"),
                rs.getString("description")
        );
    }
}