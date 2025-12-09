// src/main/java/com/bankingSystem/Interest/CheckingInterest.java
package com.bankingSystem.Interest;

public class CheckingInterest implements InterestStrategy {
    private final double rate = 0.0; // لا فائدة للحساب الجاري

    @Override
    public double calculateInterest(double balance) {
        return 0.0; // دائمًا 0
    }
}