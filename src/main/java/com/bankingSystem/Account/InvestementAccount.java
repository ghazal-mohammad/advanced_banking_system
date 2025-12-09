//package com.bankingSystem.Account;
//// src/main/java/com/bank/system/account/InvestmentAccount.java
//
//
//import java.util.Random;
//
//public class InvestementAccount extends Account {
//    private double riskLevel; // 0.0 to 1.0
//
//    public InvestementAccount(double riskLevel) {
//        this.riskLevel = riskLevel;
//    }
//
//    @Override
//    public void calculateInterest() {
//        Random random = new Random();
//        double marketReturn = 0.05 + (random.nextDouble() * 0.20 - 0.10) * riskLevel; // تقلبات
//        double gain = balance * marketReturn;
//        balance += gain;
//        addToHistory("Investment return: " + gain);
//        notifyObservers("Investment update: " + (gain > 0 ? "Gain" : "Loss") + " of " + gain);
//    }
//}


// src/main/java/com/bankingSystem/Account/InvestmentAccount.java
package com.bankingSystem.Account;

import com.bankingSystem.Interest.InterestStrategy;
import com.bankingSystem.Interest.InvestmentInterest;

import java.util.Random;

public class InvestementAccount extends Account {
    private String riskLevel; // LOW, MEDIUM, HIGH
    private InterestStrategy interestStrategy; // Strategy Pattern

    public InvestementAccount(String accountNumber, String ownerId, String riskLevel) {
        super(accountNumber, ownerId);
        this.riskLevel = riskLevel.toUpperCase();
        this.interestStrategy = new InvestmentInterest(); // ربط Strategy
    }

    @Override
    public double calculateInterest() {
        // استخدم Strategy للحساب، مع simulation للعائد الاستثماري
        double baseInterest = interestStrategy.calculateInterest(balance);

        // Simulation بسيطة للمخاطرة (random gain/loss بناءً على riskLevel)
        Random random = new Random();
        double riskMultiplier = switch (riskLevel) {
            case "LOW" -> 0.5;    // مخاطرة منخفضة: تغيير صغير
            case "MEDIUM" -> 1.0; // متوسط
            case "HIGH" -> 2.0;   // عالية: تغيير كبير
            default -> 1.0;
        };

        double volatility = (random.nextDouble() * 0.20 - 0.10) * riskMultiplier; // -10% إلى +10% مع تعديل المخاطرة
        double totalInterest = baseInterest + (balance * volatility);

        return totalInterest; // ترجع الرقم فقط، ما تعدلش balance هنا
    }

    // Getter للمخاطرة (اختياري للاستخدام في الـ toString أو Reports)
    public String getRiskLevel() {
        return riskLevel;
    }

    @Override
    public String toString() {
        return "InvestmentAccount[" + accountNumber + "] | Balance: " + String.format("%.2f", balance) +
                " | Risk: " + riskLevel + " | Expected Interest: " + String.format("%.2f", calculateInterest());
    }
}