// src/main/java/com/bankingSystem/Account/InvestmentAccount.java
package com.bankingSystem.Account;

import com.bankingSystem.Interest.InterestStrategy;
import com.bankingSystem.Interest.InvestmentInterest;

import java.util.Random;

public class InvestmentAccount extends Account {
    private String riskLevel; // LOW, MEDIUM, HIGH
    private InterestStrategy interestStrategy; // Strategy Pattern

    public InvestmentAccount(String accountNumber, String ownerId, String riskLevel) {
        super(accountNumber, ownerId);
        this.riskLevel = riskLevel.toUpperCase();
        this.interestStrategy = new InvestmentInterest(); // Bind Strategy
    }

    @Override
    public double calculateInterest() {
        // Use Strategy for calculation, with simulation for investment return
        double baseInterest = interestStrategy.calculateInterest(balance);

        // Simple simulation for risk (random gain/loss based on riskLevel)
        Random random = new Random();
        double riskMultiplier = switch (riskLevel) {
            case "LOW" -> 0.5;    // Low risk: small change
            case "MEDIUM" -> 1.0; // Medium
            case "HIGH" -> 2.0;   // High: large change
            default -> 1.0;
        };

        double volatility = (random.nextDouble() * 0.20 - 0.10) * riskMultiplier; // -10% to +10% adjusted by risk
        double totalInterest = baseInterest + (balance * volatility);

        return totalInterest; // Return the value only, do not modify balance here
    }

    // Getter for risk (optional for use in toString or Reports)
    public String getRiskLevel() {
        return riskLevel;
    }

    @Override
    public String toString() {
        return "InvestmentAccount[" + accountNumber + "] | Balance: " + String.format("%.2f", balance) +
                " | Risk: " + riskLevel + " | Expected Interest: " + String.format("%.2f", calculateInterest());
    }
}