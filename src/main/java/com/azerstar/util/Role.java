package com.azerstar.util;

/** Üç səviyyəli yetki: baş admin, filial admini, sadə istifadəçi. */
public enum Role {
    SUPER_ADMIN("Baş admin"),
    BRANCH_ADMIN("Filial admini"),
    USER("İstifadəçi");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static Role of(String dbValue) {
        try {
            return valueOf(dbValue);
        } catch (IllegalArgumentException | NullPointerException e) {
            return USER;
        }
    }
}
