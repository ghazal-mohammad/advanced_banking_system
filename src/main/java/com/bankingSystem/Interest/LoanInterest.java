// src/main/java/com/bankingSystem/Interest/LoanInterest.java
package com.bankingSystem.Interest;

public class LoanInterest implements InterestStrategy {
    private final double rate = 0.07; // 7% سنوي

    @Override
    public double calculateInterest(double balance) {
        // الفائدة على الديون (الرصيد سالب)، شهري
        double debt = Math.abs(balance); // أخذ القيمة المطلقة للديون
        return debt * rate / 12; // شهري
    }
}