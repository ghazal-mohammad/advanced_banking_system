// src/main/java/com/bankingSystem/Interest/SavingInterest.java
package com.bankingSystem.Interest;

public class SavingInterest implements InterestStrategy {
    private final double rate = 0.04; // 4% سنوي

    @Override
    public double calculateInterest(double balance) {
        if (balance < 0) return 0.0; // لا فائدة على الرصيد السالب
        return balance * rate;
    }
}