package com.azerstar.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/** Sorğu nəticəsinin bir sətri (sütun adı -> dəyər, sıra saxlanılır). */
public class Row {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final Map<String, Object> values = new LinkedHashMap<>();

    public void put(String column, Object value) {
        values.put(column, value);
    }

    public Iterable<String> columns() {
        return values.keySet();
    }

    public Object get(String column) {
        return values.get(column);
    }

    public String str(String column) {
        Object v = values.get(column);
        return v == null ? null : v.toString();
    }

    public int integer(String column) {
        Object v = values.get(column);
        return v instanceof Number n ? n.intValue() : 0;
    }

    public double number(String column) {
        Object v = values.get(column);
        return v instanceof Number n ? n.doubleValue() : 0;
    }

    public boolean isNull(String column) {
        return values.get(column) == null;
    }

    /** Cədvəldə göstərmək üçün: tarix, ədəd və boş dəyərlər səliqəli formatlanır. */
    public String text(String column) {
        return format(values.get(column));
    }

    public static String format(Object v) {
        if (v == null) return "";
        if (v instanceof byte[] b) return new String(b, java.nio.charset.StandardCharsets.UTF_8);
        if (v instanceof Timestamp t) return t.toLocalDateTime().format(DATE_TIME);
        if (v instanceof java.time.LocalDateTime t) return t.format(DATE_TIME);
        if (v instanceof Double || v instanceof Float || v instanceof BigDecimal) {
            double d = ((Number) v).doubleValue();
            if (d == Math.rint(d)) return String.valueOf((long) d);
            return String.format(java.util.Locale.ROOT, "%.3f", d).replaceAll("0+$", "").replaceAll("\\.$", "");
        }
        return v.toString();
    }
}
