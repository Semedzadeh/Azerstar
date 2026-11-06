package com.azerstar.screen.drying;

import com.azerstar.util.DataTable;
import com.azerstar.util.Db;
import com.azerstar.util.Row;
import com.azerstar.util.Screen;
import com.azerstar.util.Ui;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Bir qurutmanın təfərrüatı: qarışan mallar, nəmlik ölçmələri, bitirmə. */
public class DryingDetailScreen extends Screen {

    private final int dryingId;

    public DryingDetailScreen(int dryingId, String title, Runnable onBack) {
        super(title, onBack, false);
        this.dryingId = dryingId;
        render();
    }

    @Override
    protected void reload() {
        render();
    }

    private void render() {
        Row d;
        ObservableList<Row> sources;
        ObservableList<Row> checks;
        ObservableList<Row> outputs;
        try {
            d = Db.queryOne("SELECT d.*, t.name AS tank_name, b.name AS branch_name, "
                    + "(SELECT COALESCE(SUM(s.weight_kg), 0) FROM drying_source s WHERE s.drying_id = d.drying_id) AS total_kg, "
                    + "(d.next_check_at IS NOT NULL AND d.next_check_at <= NOW()) AS overdue "
                    + "FROM drying d LEFT JOIN tank t ON t.tank_id = d.tank_id JOIN branch b ON b.branch_id = d.branch_id WHERE d.drying_id = ?", dryingId);
            sources = Db.query("SELECT s.*, r.customer_name, r.date_time AS receipt_date FROM drying_source s "
                    + "JOIN receipt r ON r.receipt_id = s.receipt_id WHERE s.drying_id = ? ORDER BY s.source_id", dryingId);
            checks = Db.query("SELECT * FROM drying_check WHERE drying_id = ? ORDER BY check_time DESC, check_id DESC", dryingId);
            outputs = Db.query("SELECT o.*, CASE o.destination WHEN 'WAREHOUSE' THEN 'Fındıq anbarı' ELSE 'Qırma' END AS dest_text "
                    + "FROM drying_output o WHERE o.drying_id = ? ORDER BY o.output_id", dryingId);
        } catch (Db.DbException e) {
            Ui.error(e.getMessage());
            return;
        }

        boolean active = "ACTIVE".equals(d.str("status"));
        int branchId = d.integer("branch_id");
        double total = d.number("total_kg");
        String place = "TANK".equals(d.str("drying_type")) ? d.str("tank_name") : d.str("location");

        HBox stats = new HBox(12,
                Ui.stat("YER", place, false),
                Ui.stat("FİLİAL", d.str("branch_name"), false),
                Ui.stat("BAŞLAMA", d.text("start_time"), false),
                Ui.stat("VƏZİYYƏT", Ui.pill(active ? "Davam edir" : "Bitib", active ? "green" : "gray"), false),
                Ui.stat("CƏMİ", Row.format(total) + " kq", false));
        if (active) {
            boolean overdue = d.integer("overdue") == 1;
            stats.getChildren().add(Ui.stat("NÖVBƏTİ NƏMLİK YOXLAMASI",
                    d.isNull("next_check_at") ? "Təyin edilməyib" : d.text("next_check_at") + (overdue ? "  (vaxtı çatıb)" : ""), overdue));
        } else {
            stats.getChildren().add(Ui.stat("BİTMƏ", d.text("end_time"), false));
        }

        TableView<Row> sourceTable = DataTable.create("Qəbul №", "receipt_id", "Müştəri", "customer_name",
                "Qurutmaya (kq)", "weight_kg", "Əlavə edilib", "added_at");
        sourceTable.setItems(sources);
        DataTable.refit(sourceTable);
        TableView<Row> checkTable = DataTable.create("Tarix/saat", "check_time", "Ləpənin nəmliyi (%)", "kernel_moisture", "Qeyd", "note");
        checkTable.setItems(checks);
        DataTable.refit(checkTable);

        VBox left = section("Qarışan mallar", sourceTable);
        VBox right = section("Nəmlik ölçmələri", checkTable);
        if (active) {
            left.getChildren().add(Ui.button("Mal əlavə et", () -> {
                if (DryingForms.addSource(dryingId, branchId)) render();
            }));
            right.getChildren().add(Ui.primary("Nəmlik ölçməsi əlavə et", () -> {
                if (DryingForms.addCheck(dryingId)) render();
            }));
        }
        HBox tables = new HBox(14, left, right);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        VBox.setVgrow(tables, Priority.ALWAYS);

        VBox page = new VBox(14, stats, tables);
        if (active) {
            page.getChildren().add(Ui.danger("Qurutmanı bitir", () -> {
                if (DryingForms.finish(dryingId, branchId, total)) render();
            }));
        } else {
            TableView<Row> outTable = DataTable.create("Təyinat", "dest_text", "Miqdar (kq)", "weight_kg", "Qalıq (kq)", "remaining_kg", "Tarix/saat", "date_time");
            outTable.setItems(outputs);
            DataTable.refit(outTable);
            double outHeight = 56 + 44 * Math.max(1, Math.min(outputs.size(), 4));
            outTable.setMinHeight(outHeight);
            outTable.setPrefHeight(outHeight);
            outTable.setMaxHeight(outHeight);
            page.getChildren().add(section("Qurutmadan çıxan fındıq", outTable));
        }
        sourceTable.setMinHeight(190);
        checkTable.setMinHeight(190);

        // pəncərə kiçik olanda və ya çıxış cədvəli uzananda səhifə sürüşdürülə bilsin
        page.setPadding(new Insets(4, 6, 8, 6));
        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("flat-scroll");
        setContent(scroll);
    }

    private static VBox section(String title, TableView<Row> table) {
        Label l = new Label(title);
        l.getStyleClass().add("section-title");
        VBox box = new VBox(6, l, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return box;
    }
}
