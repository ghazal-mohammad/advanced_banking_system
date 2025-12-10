// src/main/java/com/bankingSystem/Database/DatabaseConnection.java (Singleton)
package com.bankingSystem.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            // H2 in-memory DB (يمكن تغيير إلى file:./bank.db للحفظ الدائم)
            connection = DriverManager.getConnection("jdbc:h2:./data/bank;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE", "sa", "");            initializeDatabase(); // إنشاء الجداول
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to database", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initializeDatabase() throws SQLException {
        String createUsers = "CREATE TABLE IF NOT EXISTS Users (" +
                "id VARCHAR(255) PRIMARY KEY, " +
                "username VARCHAR(255), " +
                "password VARCHAR(255), " +
                "role VARCHAR(50)" +
                ")";
        String createAccounts = "CREATE TABLE IF NOT EXISTS Accounts (" +
                "accountId VARCHAR(255) PRIMARY KEY, " +
                "accountNumber VARCHAR(255), " +
                "balance DOUBLE, " +
                "creationDate TIMESTAMP, " +
                "ownerId VARCHAR(255), " +
                "state VARCHAR(50), " +
                "type VARCHAR(50), " +
                "riskLevel VARCHAR(50) NULL, " +
                "loanAmount DOUBLE NULL" +
                ")";
        String createTransactions = "CREATE TABLE IF NOT EXISTS Transactions (" +
                "transactionId VARCHAR(255) PRIMARY KEY, " +
                "type VARCHAR(50), " +
                "amount DOUBLE, " +
                "timestamp TIMESTAMP, " +
                "fromAccount VARCHAR(255), " +
                "toAccount VARCHAR(255), " +
                "status VARCHAR(50), " +
                "description VARCHAR(255)" +
                ")";

        connection.createStatement().execute(createUsers);
        connection.createStatement().execute(createAccounts);
        connection.createStatement().execute(createTransactions);

        // إضافة بيانات افتراضية للاختبار
        connection.createStatement().execute("INSERT INTO Users (id, username, password, role) VALUES " +
                "('user1', 'user1', 'pass123', 'Customer'), " +
                "('admin', 'admin', 'adminpass', 'Admin')");
    }
}