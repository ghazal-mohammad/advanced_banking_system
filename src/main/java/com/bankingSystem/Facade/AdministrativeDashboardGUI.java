// src/main/java/com/bankingSystem/Facade/AdministrativeDashboardGUI.java
package com.bankingSystem.Facade;

import com.bankingSystem.Transaction.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * واجهة رسومية (GUI) للوحة التحكم الإدارية
 * تعرض سجلات التدقيق وعدد المعاملات المتزامنة
 */
public class AdministrativeDashboardGUI extends JFrame {

    // Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private final AdministrativeDashboardFacade dashboardFacade;
    // Components
    private JLabel concurrentTransactionsLabel;
    private JLabel totalTransactionsLabel;
    private JLabel completedTransactionsLabel;
    private JLabel pendingTransactionsLabel;
    private JLabel totalAmountLabel;
    private JTable auditLogsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton showSummaryButton;
    private JButton clearLogsButton;

    public AdministrativeDashboardGUI() {
        this.dashboardFacade = AdministrativeDashboardFacade.getInstance();
        initializeGUI();
        loadDashboardData();
    }

    /**
     * تشغيل الواجهة الرسومية
     */
    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Run GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AdministrativeDashboardGUI gui = new AdministrativeDashboardGUI();
                gui.setVisible(true);
            }
        });
    }

    /**
     * تهيئة الواجهة الرسومية
     */
    private void initializeGUI() {
        setTitle("لوحة التحكم الإدارية - Administrative Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel الرئيسي
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header Panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Stats Panel (Statistics)
        mainPanel.add(createStatsPanel(), BorderLayout.CENTER);

        // Audit Logs Panel
        mainPanel.add(createAuditLogsPanel(), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // Toolbar Panel
        add(createToolbarPanel(), BorderLayout.NORTH);
    }

    /**
     * إنشاء Header Panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel titleLabel = new JLabel("لوحة التحكم الإدارية - Administrative Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel);

        return headerPanel;
    }

    /**
     * إنشاء Statistics Panel
     */
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("إحصائيات النظام - System Statistics"));

        // Concurrent Transactions Card
        JPanel concurrentPanel = createStatCard(
                "المعاملات المتزامنة",
                "Concurrent Transactions",
                "0",
                PRIMARY_COLOR
        );
        concurrentTransactionsLabel = (JLabel) concurrentPanel.getClientProperty("valueLabel");
        statsPanel.add(concurrentPanel);

        // Total Transactions Card
        JPanel totalPanel = createStatCard(
                "إجمالي المعاملات",
                "Total Transactions",
                "0",
                new Color(52, 152, 219)
        );
        totalTransactionsLabel = (JLabel) totalPanel.getClientProperty("valueLabel");
        statsPanel.add(totalPanel);

        // Completed Transactions Card
        JPanel completedPanel = createStatCard(
                "المعاملات المكتملة",
                "Completed Transactions",
                "0",
                SUCCESS_COLOR
        );
        completedTransactionsLabel = (JLabel) completedPanel.getClientProperty("valueLabel");
        statsPanel.add(completedPanel);

        // Pending Transactions Card
        JPanel pendingPanel = createStatCard(
                "المعاملات المعلقة",
                "Pending Transactions",
                "0",
                WARNING_COLOR
        );
        pendingTransactionsLabel = (JLabel) pendingPanel.getClientProperty("valueLabel");
        statsPanel.add(pendingPanel);

        // Total Amount Card
        JPanel amountPanel = createStatCard(
                "إجمالي المبلغ",
                "Total Amount",
                "0.00",
                new Color(155, 89, 182)
        );
        totalAmountLabel = (JLabel) amountPanel.getClientProperty("valueLabel");
        statsPanel.add(amountPanel);

        // Empty panel for spacing
        statsPanel.add(new JPanel());

        return statsPanel;
    }

    /**
     * إنشاء Stat Card
     */
    private JPanel createStatCard(String titleAr, String titleEn, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("<html><center>" + titleAr + "<br>" + titleEn + "</center></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titlePanel.add(titleLabel);
        card.add(titlePanel, BorderLayout.NORTH);

        // Value
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        valuePanel.setBackground(Color.WHITE);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valuePanel.add(valueLabel);
        card.add(valuePanel, BorderLayout.CENTER);

        // Store the label reference in the card for later access
        card.putClientProperty("valueLabel", valueLabel);

        return card;
    }

    /**
     * إنشاء Audit Logs Panel
     */
    private JPanel createAuditLogsPanel() {
        JPanel logsPanel = new JPanel(new BorderLayout(5, 5));
        logsPanel.setBorder(BorderFactory.createTitledBorder("سجلات التدقيق - Audit Logs"));

        // Table Model
        String[] columnNames = {
                "ID", "النوع / Type", "المبلغ / Amount",
                "من / From", "إلى / To", "الحالة / Status",
                "الوقت / Time", "منفذ من / Performed By"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        auditLogsTable = new JTable(tableModel);
        auditLogsTable.setFont(new Font("Arial", Font.PLAIN, 11));
        auditLogsTable.setRowHeight(25);
        auditLogsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        auditLogsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Make headers bold
        auditLogsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        auditLogsTable.getTableHeader().setBackground(PRIMARY_COLOR);
        auditLogsTable.getTableHeader().setForeground(Color.WHITE);

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(auditLogsTable);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        logsPanel.add(scrollPane, BorderLayout.CENTER);

        return logsPanel;
    }

    /**
     * إنشاء Toolbar Panel
     */
    private JPanel createToolbarPanel() {
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Refresh Button
        refreshButton = new JButton("تحديث / Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBackground(PRIMARY_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setPreferredSize(new Dimension(120, 35));
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDashboardData();
                JOptionPane.showMessageDialog(AdministrativeDashboardGUI.this,
                        "تم تحديث البيانات بنجاح\nData refreshed successfully",
                        "تحديث / Refresh",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        toolbarPanel.add(refreshButton);

        // Show Summary Button
        showSummaryButton = new JButton("عرض الملخص / Show Summary");
        showSummaryButton.setFont(new Font("Arial", Font.BOLD, 12));
        showSummaryButton.setBackground(SUCCESS_COLOR);
        showSummaryButton.setForeground(Color.WHITE);
        showSummaryButton.setPreferredSize(new Dimension(180, 35));
        showSummaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSummaryDialog();
            }
        });
        toolbarPanel.add(showSummaryButton);

        // Clear Logs Button (clears table only, not database)
        clearLogsButton = new JButton("مسح الجدول / Clear Table");
        clearLogsButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearLogsButton.setBackground(DANGER_COLOR);
        clearLogsButton.setForeground(Color.WHITE);
        clearLogsButton.setPreferredSize(new Dimension(150, 35));
        clearLogsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(
                        AdministrativeDashboardGUI.this,
                        "هل تريد مسح الجدول؟ (لن يتم حذف البيانات من قاعدة البيانات)\n" +
                                "Clear table? (Data will NOT be deleted from database)",
                        "تأكيد / Confirm",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (option == JOptionPane.YES_OPTION) {
                    tableModel.setRowCount(0);
                    JOptionPane.showMessageDialog(AdministrativeDashboardGUI.this,
                            "تم مسح الجدول\nTable cleared");
                }
            }
        });
        toolbarPanel.add(clearLogsButton);

        return toolbarPanel;
    }

    /**
     * تحميل بيانات لوحة التحكم
     */
    private void loadDashboardData() {
        // Load Statistics
        AdministrativeDashboardFacade.DashboardSummary summary = dashboardFacade.getDashboardSummary();

        concurrentTransactionsLabel.setText(String.valueOf(summary.getConcurrentTransactions()));
        totalTransactionsLabel.setText(String.valueOf(summary.getTotalTransactions()));
        completedTransactionsLabel.setText(String.valueOf(summary.getCompletedTransactions()));
        pendingTransactionsLabel.setText(String.valueOf(summary.getPendingTransactions()));
        totalAmountLabel.setText(String.format("%.2f", summary.getTotalAmount()));

        // Load Audit Logs
        List<Transaction> auditLogs = dashboardFacade.getAuditLogs();

        // Clear existing rows
        tableModel.setRowCount(0);

        // Add transactions (limit to last 100 for performance)
        int maxRows = Math.min(100, auditLogs.size());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (int i = 0; i < maxRows; i++) {
            Transaction tx = auditLogs.get(i);

            String id = tx.getTransactionId().substring(0, 8);
            String type = tx.getType();
            String amount = String.format("%.2f", tx.getAmount());
            String from = tx.getFromAccount() != null ? tx.getFromAccount() : "-";
            String to = tx.getToAccount() != null ? tx.getToAccount() : "-";
            String status = tx.getStatus();
            String time = (tx.getPerformedAt() != null ? tx.getPerformedAt() : tx.getTimestamp())
                    .format(formatter);
            String performedBy = tx.getPerformedBy() != null ? tx.getPerformedBy() : "SYSTEM";

            tableModel.addRow(new Object[]{id, type, amount, from, to, status, time, performedBy});
        }

        // Update table
        auditLogsTable.revalidate();
        auditLogsTable.repaint();
    }

    /**
     * عرض نافذة الملخص
     */
    private void showSummaryDialog() {
        AdministrativeDashboardFacade.DashboardSummary summary = dashboardFacade.getDashboardSummary();

        String summaryText = String.format(
                "<html><body style='font-family: Arial; font-size: 14px; padding: 10px;'>" +
                        "<h2 style='color: #2980b9;'>ملخص لوحة التحكم</h2>" +
                        "<h2 style='color: #2980b9;'>Dashboard Summary</h2>" +
                        "<hr>" +
                        "<p><b>إجمالي المعاملات / Total Transactions:</b> %d</p>" +
                        "<p><b>المعاملات المكتملة / Completed:</b> %d</p>" +
                        "<p><b>المعاملات المعلقة / Pending:</b> %d</p>" +
                        "<p><b>إجمالي المبلغ / Total Amount:</b> %.2f</p>" +
                        "<p><b>المعاملات المتزامنة / Concurrent:</b> %d</p>" +
                        "</body></html>",
                summary.getTotalTransactions(),
                summary.getCompletedTransactions(),
                summary.getPendingTransactions(),
                summary.getTotalAmount(),
                summary.getConcurrentTransactions()
        );

        JLabel summaryLabel = new JLabel(summaryText);
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JOptionPane.showMessageDialog(
                this,
                summaryLabel,
                "ملخص لوحة التحكم / Dashboard Summary",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
