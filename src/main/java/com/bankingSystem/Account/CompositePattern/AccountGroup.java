// src/main/java/com/bankingSystem/Account/CompositePattern/AccountGroup.java
package com.bankingSystem.Account.CompositePattern;

import java.util.ArrayList;
import java.util.List;

public class AccountGroup implements AccountComponent {

    private final String groupId;
    private final String groupName;
    private final List<AccountComponent> children;

    public AccountGroup(String groupName) {
        this.groupId = java.util.UUID.randomUUID().toString();
        this.groupName = groupName;
        this.children = new ArrayList<>();
    }

    // === Composite Pattern Methods ===

    @Override
    public void add(AccountComponent component) {
        children.add(component);
    }

    @Override
    public void remove(AccountComponent component) {
        children.remove(component);
    }

    @Override
    public void showDetails() {
        System.out.println("════════════════════════════════════════════════");
        System.out.println("Group Account: " + groupName);
        System.out.println("ID: " + groupId);
        System.out.println("Number of Sub-Accounts: " + children.size());
        System.out.println("────────────────────────────────────────────────");
        for (AccountComponent component : children) {
            component.showDetails();
        }
        System.out.println("────────────────────────────────────────────────");
        System.out.printf("Total Balance in Group: %,.2f%n", getTotalBalance());
        System.out.println("════════════════════════════════════════════════\n");
    }

    @Override
    public double getTotalBalance() {
        return children.stream()
                .mapToDouble(AccountComponent::getTotalBalance)
                .sum();
    }

    // Added: Getter for children (for Reports or Inquiries)
    public List<AccountComponent> getChildren() {
        return new ArrayList<>(children);
    }
}