package com.azerstar.util;

/** İstifadəçi icazələrinin verildiyi bölmələr. */
public enum Section {
    RECEIPT("Mal qəbulu"),
    ANALYSIS("Analiz"),
    DRYING("Qurutma"),
    WAREHOUSE("Anbar"),
    REPORT("Hesabat"),
    SETTINGS("Ayarlar");

    private final String title;

    Section(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
}
