// src/main/java/com/bank/account/composite/AccountComponent.java

package com.bankingSystem.Account.CompositePattern;

public interface AccountComponent {

    void showDetails();
    double getTotalBalance();

    // للـ Composite فقط
    default void add(AccountComponent component) {
        throw new UnsupportedOperationException("لا يمكن إضافة حساب فرعي لهذا النوع");
    }

    default void remove(AccountComponent component) {
        throw new UnsupportedOperationException("لا يمكن حذف حساب فرعي من هذا النوع");
    }
}