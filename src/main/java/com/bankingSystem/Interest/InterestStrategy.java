// src/main/java/com/bankingSystem/Interest/InterestStrategy.java
package com.bankingSystem.Interest;

public interface InterestStrategy {
    /**
     * يحسب الفائدة بناءً على الرصيد الحالي للحساب.
     *
     * @param balance الرصيد الحالي للحساب.
     * @return قيمة الفائدة المحسوبة (double).
     */
    double calculateInterest(double balance);
}