package com.azerstar.util;

/** Sadə istifadəçilərə icazə verilən iş bölmələri (adminlərin ayarları rola görə ayrıca idarə olunur). */
public enum Section {
    RECEIPT("Mal qəbulu"),
    ANALYSIS("Analiz"),
    DRYING("Qurutma"),
    WAREHOUSE("Anbar"),
    REPORT("Hesabat");

    private final String title;

    Section(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
}
