// src/main/java/com/bankingSystem/Interest/InvestmentInterest.java
package com.bankingSystem.Interest;

import java.util.Random;

public class InvestmentInterest implements InterestStrategy {
    private final double baseRate = 0.09; // 9% أساسي

    @Override
    public double calculateInterest(double balance) {
        if (balance <= 0) return 0.0; // لا فائدة على الرصيد غير الإيجابي

        Random random = new Random();
        // Simulation للمخاطرة: تغيير عشوائي بين -10% إلى +10%
        double volatility = (random.nextDouble() * 0.20 - 0.10); // -0.10 إلى +0.10
        return balance * (baseRate + volatility);
    }
}