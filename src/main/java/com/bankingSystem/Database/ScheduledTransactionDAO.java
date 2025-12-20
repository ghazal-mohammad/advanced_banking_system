package com.bankingSystem.Database;

import com.bankingSystem.Transaction.ScheduledTransaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO للتعامل مع المعاملات المجدولة في قاعدة البيانات
 */
public class ScheduledTransactionDAO {
    private final Connection conn = DatabaseConnection.getInstance().getConnection();

    /**
     * حفظ معاملة مجدولة في قاعدة البيانات
     */
    public void saveScheduledTransaction(ScheduledTransaction scheduledTx) {
        String sql = """
                MERGE INTO ScheduledTransactions KEY(scheduledTransactionId)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, scheduledTx.getScheduledTransactionId());
            pstmt.setString(2, scheduledTx.getTransactionType());
            pstmt.setDouble(3, scheduledTx.getAmount());
            pstmt.setString(4, scheduledTx.getFromAccountNumber());
            pstmt.setString(5, scheduledTx.getToAccountNumber());
            pstmt.setString(6, scheduledTx.getStrategyType());
            pstmt.setLong(7, scheduledTx.getIntervalMinutes());
            pstmt.setObject(8, scheduledTx.getNextExecutionTime());
            pstmt.setObject(9, scheduledTx.getCreatedAt());
            pstmt.setBoolean(10, scheduledTx.isActive());
            pstmt.setString(11, scheduledTx.getCreatedBy());
            pstmt.setObject(12, scheduledTx.getLastExecutionTime());
            pstmt.setInt(13, scheduledTx.getExecutionCount());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving scheduled transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * تحميل جميع المعاملات المجدولة النشطة
     */
    public List<ScheduledTransaction> loadActiveScheduledTransactions() {
        List<ScheduledTransaction> scheduledTransactions = new ArrayList<>();
        String sql = "SELECT * FROM ScheduledTransactions WHERE isActive = TRUE";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                scheduledTransactions.add(buildScheduledTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error loading active scheduled transactions");
            e.printStackTrace();
        }

        return scheduledTransactions;
    }

    /**
     * تحميل جميع المعاملات المجدولة
     */
    public List<ScheduledTransaction> loadAllScheduledTransactions() {
        List<ScheduledTransaction> scheduledTransactions = new ArrayList<>();
        String sql = "SELECT * FROM ScheduledTransactions";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                scheduledTransactions.add(buildScheduledTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error loading all scheduled transactions");
            e.printStackTrace();
        }

        return scheduledTransactions;
    }

    /**
     * تحميل معاملة مجدولة بواسطة ID
     */
    public ScheduledTransaction loadScheduledTransactionById(String scheduledTransactionId) {
        String sql = "SELECT * FROM ScheduledTransactions WHERE scheduledTransactionId = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, scheduledTransactionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return buildScheduledTransactionFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading scheduled transaction by ID: " + scheduledTransactionId);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * تحديث حالة المعاملة المجدولة (نشطة/غير نشطة)
     */
    public void updateScheduledTransactionStatus(String scheduledTransactionId, boolean isActive) {
        String sql = "UPDATE ScheduledTransactions SET isActive = ? WHERE scheduledTransactionId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isActive);
            pstmt.setString(2, scheduledTransactionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating scheduled transaction status: " + e.getMessage());
        }
    }

    /**
     * حذف معاملة مجدولة
     */
    public void deleteScheduledTransaction(String scheduledTransactionId) {
        String sql = "DELETE FROM ScheduledTransactions WHERE scheduledTransactionId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, scheduledTransactionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting scheduled transaction: " + e.getMessage());
        }
    }

    /**
     * Helper method to build ScheduledTransaction from ResultSet
     */
    private ScheduledTransaction buildScheduledTransactionFromResultSet(ResultSet rs) throws SQLException {
        return new ScheduledTransaction(
                rs.getString("scheduledTransactionId"),
                rs.getString("transactionType"),
                rs.getDouble("amount"),
                rs.getString("fromAccountNumber"),
                rs.getString("toAccountNumber"),
                rs.getString("strategyType"),
                rs.getLong("intervalMinutes"),
                rs.getObject("nextExecutionTime", LocalDateTime.class),
                rs.getObject("createdAt", LocalDateTime.class),
                rs.getBoolean("isActive"),
                rs.getString("createdBy"),
                rs.getObject("lastExecutionTime", LocalDateTime.class) != null
                        ? rs.getObject("lastExecutionTime", LocalDateTime.class) : null,
                rs.getInt("executionCount")
        );
    }
}
