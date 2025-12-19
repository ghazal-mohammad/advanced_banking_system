// src/main/java/com/bankingSystem/Roles/Role.java
package com.bankingSystem.user;

public enum Role {
    CUSTOMER("Customer"),
    TELLER("Teller"),
    MANAGER("Manager"),
    ADMIN("Admin");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role fromString(String text) {
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(text) || r.displayName.equalsIgnoreCase(text)) {
                return r;
            }
        }
        throw new IllegalArgumentException("No role with text " + text + " found");
    }
}