// src/main/java/com/bank/account/composite/AccountGroup.java

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
        System.out.println(" حساب جماعي: " + groupName);
        System.out.println(" المعرف: " + groupId);
        System.out.println(" عدد الحسابات الفرعية: " + children.size() + " حساب");
        System.out.println("────────────────────────────────────────────────");
        for (AccountComponent component : children) {
            component.showDetails();
        }
        System.out.println("────────────────────────────────────────────────");
        System.out.printf(" إجمالي الرصيد في المجموعة: %,.2f جنيه%n", getTotalBalance());
        System.out.println("════════════════════════════════════════════════\n");
    }

    @Override
    public double getTotalBalance() {
        return children.stream()
                .mapToDouble(AccountComponent::getTotalBalance)
                .sum();
    }
//
//    // === Getters (اختياري للاستخدام المستقبلي) ===
//    public String getGroupId()
}