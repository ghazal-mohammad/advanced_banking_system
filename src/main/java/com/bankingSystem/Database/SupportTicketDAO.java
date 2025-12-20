package com.bankingSystem.Database;

import com.bankingSystem.CustomerService.SupportTicket;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO للتعامل مع تذاكر الدعم في قاعدة البيانات
 */
public class SupportTicketDAO {
    private final Connection conn = DatabaseConnection.getInstance().getConnection();

    /**
     * حفظ تذكرة دعم في قاعدة البيانات
     */
    public void saveSupportTicket(SupportTicket ticket) {
        String sql = """
                MERGE INTO SupportTickets KEY(ticketId)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ticket.getTicketId());
            pstmt.setString(2, ticket.getCustomerId());
            pstmt.setString(3, ticket.getSubject());
            pstmt.setString(4, ticket.getDescription());
            pstmt.setString(5, ticket.getStatus().name());
            pstmt.setString(6, ticket.getPriority().name());
            pstmt.setObject(7, ticket.getCreatedAt());
            pstmt.setObject(8, ticket.getUpdatedAt());
            pstmt.setString(9, ticket.getAssignedTo());
            pstmt.setString(10, ticket.getResolution());
            pstmt.setString(11, ticket.getCategory().name());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving support ticket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * تحميل جميع تذاكر الدعم
     */
    public List<SupportTicket> loadAllSupportTickets() {
        List<SupportTicket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM SupportTickets ORDER BY createdAt DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tickets.add(buildSupportTicketFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error loading all support tickets");
            e.printStackTrace();
        }

        return tickets;
    }

    /**
     * تحميل تذاكر عميل معين
     */
    public List<SupportTicket> loadTicketsByCustomer(String customerId) {
        List<SupportTicket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM SupportTickets WHERE customerId = ? ORDER BY createdAt DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(buildSupportTicketFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading tickets for customer: " + customerId);
            e.printStackTrace();
        }

        return tickets;
    }

    /**
     * تحميل تذاكر مفتوحة أو قيد المعالجة
     */
    public List<SupportTicket> loadOpenTickets() {
        List<SupportTicket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM SupportTickets WHERE status IN ('OPEN', 'IN_PROGRESS') ORDER BY priority DESC, createdAt ASC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tickets.add(buildSupportTicketFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error loading open tickets");
            e.printStackTrace();
        }

        return tickets;
    }

    /**
     * تحميل تذكرة بواسطة ID
     */
    public SupportTicket loadTicketById(String ticketId) {
        String sql = "SELECT * FROM SupportTickets WHERE ticketId = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ticketId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return buildSupportTicketFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading ticket by ID: " + ticketId);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Helper method to build SupportTicket from ResultSet
     */
    private SupportTicket buildSupportTicketFromResultSet(ResultSet rs) throws SQLException {
        return new SupportTicket(
                rs.getString("ticketId"),
                rs.getString("customerId"),
                rs.getString("subject"),
                rs.getString("description"),
                SupportTicket.TicketStatus.valueOf(rs.getString("status")),
                SupportTicket.TicketPriority.valueOf(rs.getString("priority")),
                rs.getObject("createdAt", LocalDateTime.class),
                rs.getObject("updatedAt", LocalDateTime.class),
                rs.getString("assignedTo"),
                rs.getString("resolution"),
                SupportTicket.TicketCategory.valueOf(rs.getString("category"))
        );
    }
}
