package com.azerstar.util;

import java.util.List;

/** Sadə istifadəçilərə icazə verilən iş bölmələri (adminlərin ayarları rola görə ayrıca idarə olunur). */
public enum Section {
    RECEIPT_RETAIL("Pərakəndə mal qəbulu"),
    RECEIPT_WHOLESALE("Topdan mal qəbulu"),
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

    /** Bazadakı bölmə adını açır; köhnə RECEIPT icazəsi hər iki qəbul növünü verir, naməlum ad boş qalır. */
    public static List<Section> parse(String name) {
        if ("RECEIPT".equals(name)) return List.of(RECEIPT_RETAIL, RECEIPT_WHOLESALE);
        try {
            return List.of(valueOf(name));
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }
}
