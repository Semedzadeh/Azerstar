package com.azerstar.util;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Map;
import java.util.WeakHashMap;

/** Row əsaslı cədvəl yaradır. */
public final class DataTable {

    private static final Font CELL_FONT = Font.font("Segoe UI", 14);
    private static final Font HEADER_FONT = Font.font("Segoe UI", FontWeight.BOLD, 12);

    // sütun -> sorğudakı açar (məzmuna görə enini hesablamaq üçün)
    private static final Map<TableColumn<Row, String>, String> KEYS = new WeakHashMap<>();

    private DataTable() {
    }

    /** Cüt-cüt: sütun başlığı, sorğudakı sütun adı. */
    public static TableView<Row> create(String... titleKeyPairs) {
        TableView<Row> table = new TableView<>();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Məlumat yoxdur"));
        for (int i = 0; i + 1 < titleKeyPairs.length; i += 2) {
            table.getColumns().add(column(titleKeyPairs[i], titleKeyPairs[i + 1]));
        }
        table.widthProperty().addListener((o, a, b) -> fit(table));
        return table;
    }

    private static TableColumn<Row, String> column(String title, String key) {
        TableColumn<Row, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().text(key)));
        col.setSortable(false);
        KEYS.put(col, key);
        return col;
    }

    private static double measure(String s, Font font) {
        Text t = new Text(s == null ? "" : s);
        t.setFont(font);
        return t.getLayoutBounds().getWidth();
    }

    /** Sütun enlərini məzmuna görə təyin edir, yer qalırsa bərabər paylayır. */
    public static void fit(TableView<Row> table) {
        if (table.getWidth() <= 0 || table.getColumns().isEmpty()) return;
        int n = table.getColumns().size();
        double[] widths = new double[n];
        double sum = 0;
        for (int i = 0; i < n; i++) {
            @SuppressWarnings("unchecked")
            TableColumn<Row, String> col = (TableColumn<Row, String>) table.getColumns().get(i);
            // başlıq ən çox iki sətrə bölünə bilər
            double header = Math.min(measure(col.getText(), HEADER_FONT) + 36, 170);
            double cells = 0;
            ObservableList<Row> items = table.getItems();
            if (items != null) {
                String key = KEYS.getOrDefault(col, col.getText());
                int limit = Math.min(items.size(), 60);
                for (int r = 0; r < limit; r++) {
                    cells = Math.max(cells, measure(items.get(r).text(key), CELL_FONT));
                }
            }
            widths[i] = Math.max(70, Math.min(Math.max(header, cells + 30), 340));
            sum += widths[i];
        }
        double available = table.getWidth() - 24;
        double factor = sum < available ? available / sum : 1;
        for (int i = 0; i < n; i++) {
            double w = widths[i] * factor;
            TableColumn<Row, ?> col = table.getColumns().get(i);
            col.setMinWidth(w);
            col.setPrefWidth(w);
            col.setMaxWidth(w);
        }
    }

    /** Hesabatlar üçün: sütunlar nəticənin özündən götürülür. */
    public static void fillAuto(TableView<Row> table, ObservableList<Row> rows) {
        table.getColumns().clear();
        if (!rows.isEmpty()) {
            for (String key : rows.get(0).columns()) {
                table.getColumns().add(column(key, key));
            }
        }
        table.setItems(rows);
        refit(table);
    }

    /** Məlumat dəyişəndən sonra enləri yenidən hesablayır. */
    public static void refit(TableView<Row> table) {
        Platform.runLater(() -> fit(table));
    }
}
