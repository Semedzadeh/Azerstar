package com.azerstar.screen.report;

import com.azerstar.screen.Menus;
import com.azerstar.util.DataTable;
import com.azerstar.util.Db;
import com.azerstar.util.Formulas;
import com.azerstar.util.Row;
import com.azerstar.util.Screen;
import com.azerstar.util.Session;
import com.azerstar.util.Ui;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Bütün proseslər üzrə hesabatlar: tarix aralığı, CSV-yə ixrac, qəbuldan ləpəyə qədər izlənmə. */
public class ReportScreen extends Screen {

    private record Def(String name, String sql, String dateColumn, String branchColumn, String order) {
        @Override
        public String toString() {
            return name;
        }
    }

    private final List<Def> defs = new ArrayList<>();
    private final ComboBox<Def> typeBox = new ComboBox<>();
    private final DatePicker from = new DatePicker();
    private final DatePicker to = new DatePicker();
    private final TableView<Row> table = DataTable.create();
    private final Label countLabel = new Label();

    public ReportScreen() {
        super("Hesabatlar", Menus::main, true);
        defineReports();

        typeBox.getItems().addAll(defs);
        typeBox.setValue(defs.get(0));
        typeBox.setPrefWidth(230);
        from.setPrefWidth(150);
        to.setPrefWidth(150);
        typeBox.setOnAction(e -> reload());
        from.setPromptText("Başlanğıc tarix");
        to.setPromptText("Son tarix");

        HBox filters = new HBox(10, new Label("Hesabat:"), typeBox, from, to,
                Ui.primary("Göstər", this::reload), Ui.button("CSV-yə ixrac et", this::export));
        filters.setAlignment(Pos.CENTER_LEFT);
        for (javafx.scene.Node n : filters.getChildren()) {
            if (n instanceof javafx.scene.layout.Region r) r.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        }

        VBox.setVgrow(table, Priority.ALWAYS);
        setContent(new VBox(10, filters, table, countLabel));
        reload();
    }

    private void defineReports() {
        defs.add(new Def("Mal qəbulu",
                "SELECT r.date_time AS `Tarix/saat`, b.name AS `Filial`, CASE r.receipt_type WHEN 'RETAIL' THEN 'Pərakəndə' ELSE 'Topdan' END AS `Növ`, "
                        + "r.customer_name AS `Müştəri`, r.weight_kg AS `Miqdar (kq)`, r.unit_price AS `Qiymət (AZN/kq)`, r.total_amount AS `Cəm (AZN)`, r.note AS `Qeyd` "
                        + "FROM receipt r JOIN branch b ON b.branch_id = r.branch_id WHERE 1 = 1",
                "r.date_time", "r.branch_id", " ORDER BY r.date_time DESC"));

        defs.add(new Def("Analiz",
                "SELECT a.date_time AS `Tarix/saat`, b.name AS `Filial`, r.customer_name AS `Müştəri`, r.weight_kg AS `Məhsul miqdarı (kq)`, "
                        + "a.sample_weight_g AS `Nümunə (q)`, a.kernel_weight_g AS `Ləpə (q)`, " + Formulas.kernelPercentSql("a") + " AS `Ləpə faizi (%)`, "
                        + "a.mindal_g AS `Mindal (q)`, a.rotten_g AS `Çürük (q)`, a.kernel_moisture AS `Ləpənin nəmliyi (%)`, a.note AS `Qeyd` "
                        + "FROM analysis a JOIN receipt r ON r.receipt_id = a.receipt_id JOIN branch b ON b.branch_id = a.branch_id WHERE 1 = 1",
                "a.date_time", "a.branch_id", " ORDER BY a.date_time DESC"));

        defs.add(new Def("Qurutma",
                "SELECT d.drying_id AS `№`, b.name AS `Filial`, COALESCE(t.name, d.location) AS `Yer`, d.start_time AS `Başlama`, d.end_time AS `Bitmə`, "
                        + "CASE d.status WHEN 'ACTIVE' THEN 'Davam edir' ELSE 'Bitib' END AS `Vəziyyət`, "
                        + "(SELECT COALESCE(SUM(s.weight_kg), 0) FROM drying_source s WHERE s.drying_id = d.drying_id) AS `Giriş (kq)`, "
                        + "(SELECT GROUP_CONCAT(CONCAT(r.customer_name, ' ', ROUND(s.weight_kg, 2), ' kq') SEPARATOR '; ') FROM drying_source s "
                        + "JOIN receipt r ON r.receipt_id = s.receipt_id WHERE s.drying_id = d.drying_id) AS `Qarışan mallar`, "
                        + "(SELECT c.kernel_moisture FROM drying_check c WHERE c.drying_id = d.drying_id ORDER BY c.check_time DESC, c.check_id DESC LIMIT 1) AS `Son nəmlik (%)`, "
                        + "(SELECT COALESCE(SUM(o.weight_kg), 0) FROM drying_output o WHERE o.drying_id = d.drying_id) AS `Çıxış (kq)` "
                        + "FROM drying d LEFT JOIN tank t ON t.tank_id = d.tank_id JOIN branch b ON b.branch_id = d.branch_id WHERE 1 = 1",
                "d.start_time", "d.branch_id", " ORDER BY d.start_time DESC"));

        defs.add(new Def("Qırma",
                "SELECT s.shelling_id AS `№`, s.date_time AS `Tarix/saat`, b.name AS `Filial`, o.drying_id AS `Qurutma №`, COALESCE(t.name, d.location) AS `Qurutma yeri`, "
                        + "s.input_kg AS `Qırmaya verilən (kq)`, "
                        + "COALESCE((SELECT SUM(k.weight_kg) FROM kernel_stock k WHERE k.shelling_id = s.shelling_id), 0) AS `Çıxan ləpə (kq)`, s.note AS `Qeyd` "
                        + "FROM shelling s JOIN branch b ON b.branch_id = s.branch_id JOIN drying_output o ON o.output_id = s.output_id "
                        + "JOIN drying d ON d.drying_id = o.drying_id LEFT JOIN tank t ON t.tank_id = d.tank_id WHERE 1 = 1",
                "s.date_time", "s.branch_id", " ORDER BY s.date_time DESC"));

        defs.add(new Def("Hazır ləpə",
                "SELECT k.kernel_id AS `S/N`, k.date_time AS `Tarix/saat`, b.name AS `Filial`, k.kernel_type AS `Ləpə növü`, k.kernel_size AS `Ləpə ölçüsü`, "
                        + "k.weight_kg AS `İlkin (kq)`, k.remaining_kg AS `Qalıq (kq)`, k.moisture AS `Nəmlik (%)`, "
                        + "CASE WHEN k.shelling_id IS NOT NULL THEN CONCAT('Qırma №', k.shelling_id) "
                        + "WHEN k.parent_kernel_id IS NOT NULL THEN CONCAT('Transfer (S/N ', k.parent_kernel_id, ')') ELSE 'Əl ilə' END AS `Mənbə`, k.note AS `Qeyd` "
                        + "FROM kernel_stock k JOIN branch b ON b.branch_id = k.branch_id WHERE 1 = 1",
                "k.date_time", "k.branch_id", " ORDER BY k.date_time DESC"));

        defs.add(new Def("Transfer edilmiş ləpə",
                "SELECT t.transfer_id AS `№`, t.sent_time AS `Göndərilmə`, fb.name AS `Haradan`, tb.name AS `Hara`, k.kernel_type AS `Ləpə növü`, k.kernel_size AS `Ləpə ölçüsü`, "
                        + "t.weight_kg AS `Miqdar (kq)`, CASE t.status WHEN 'SENT' THEN 'Təsdiq gözləyir' WHEN 'RECEIVED' THEN 'Qəbul edilib' ELSE 'Ləğv edilib' END AS `Vəziyyət`, "
                        + "t.received_time AS `Qəbul tarixi`, t.note AS `Qeyd` "
                        + "FROM kernel_transfer t JOIN branch fb ON fb.branch_id = t.from_branch_id JOIN branch tb ON tb.branch_id = t.to_branch_id "
                        + "JOIN kernel_stock k ON k.kernel_id = t.kernel_id WHERE 1 = 1",
                "t.sent_time", "t.from_branch_id|t.to_branch_id", " ORDER BY t.sent_time DESC"));

        defs.add(new Def("Ləpə satışları",
                "SELECT s.date_time AS `Tarix/saat`, b.name AS `Filial`, CASE s.sale_type WHEN 'RETAIL' THEN 'Pərakəndə' ELSE 'Topdan' END AS `Satış növü`, "
                        + "s.customer_name AS `Müştəri`, k.kernel_type AS `Ləpə növü`, k.kernel_size AS `Ləpə ölçüsü`, s.weight_kg AS `Miqdar (kq)`, "
                        + "s.unit_price AS `Qiymət (AZN/kq)`, s.total_amount AS `Cəm (AZN)`, s.note AS `Qeyd` "
                        + "FROM kernel_sale s JOIN branch b ON b.branch_id = s.branch_id JOIN kernel_stock k ON k.kernel_id = s.kernel_id WHERE 1 = 1",
                "s.date_time", "s.branch_id", " ORDER BY s.date_time DESC"));

        defs.add(new Def("İzlənmə (qəbuldan ləpəyə)",
                "SELECT r.receipt_id AS `Qəbul №`, r.date_time AS `Qəbul tarixi`, b.name AS `Filial`, r.customer_name AS `Müştəri`, r.weight_kg AS `Qəbul (kq)`, "
                        + "(SELECT " + Formulas.kernelPercentSql("a") + " FROM analysis a WHERE a.receipt_id = r.receipt_id ORDER BY a.date_time DESC, a.analysis_id DESC LIMIT 1) AS `Son analiz: ləpə faizi (%)`, "
                        + "d.drying_id AS `Qurutma №`, COALESCE(t.name, d.location) AS `Qurutma yeri`, s.weight_kg AS `Bu qəbuldan qurutmaya (kq)`, "
                        + "sh.shelling_id AS `Qırma №`, sh.input_kg AS `Qırmaya verilən (kq)`, "
                        + "k.kernel_id AS `Ləpə S/N`, k.kernel_type AS `Ləpə növü`, k.kernel_size AS `Ləpə ölçüsü`, k.weight_kg AS `Ləpə (kq)` "
                        + "FROM receipt r JOIN branch b ON b.branch_id = r.branch_id "
                        + "LEFT JOIN drying_source s ON s.receipt_id = r.receipt_id LEFT JOIN drying d ON d.drying_id = s.drying_id "
                        + "LEFT JOIN tank t ON t.tank_id = d.tank_id LEFT JOIN drying_output o ON o.drying_id = d.drying_id "
                        + "LEFT JOIN shelling sh ON sh.output_id = o.output_id LEFT JOIN kernel_stock k ON k.shelling_id = sh.shelling_id WHERE 1 = 1",
                "r.date_time", "r.branch_id", " ORDER BY r.date_time DESC, d.drying_id, sh.shelling_id"));
    }

    @Override
    protected void reload() {
        Def def = typeBox.getValue();
        if (def == null) return;
        StringBuilder sql = new StringBuilder(def.sql());
        List<Object> params = new ArrayList<>();
        sql.append(branchFilter(def.branchColumn()));
        LocalDate f = from.getValue();
        LocalDate t = to.getValue();
        if (f != null) {
            sql.append(" AND ").append(def.dateColumn()).append(" >= ?");
            params.add(f.atStartOfDay());
        }
        if (t != null) {
            sql.append(" AND ").append(def.dateColumn()).append(" < ?");
            params.add(t.plusDays(1).atStartOfDay());
        }
        sql.append(def.order());
        Ui.run(() -> {
            ObservableList<Row> rows = Db.query(sql.toString(), params.toArray());
            DataTable.fillAuto(table, rows);
            countLabel.setText("Sətir sayı: " + rows.size());
        });
    }

    // "a|b" yazılıbsa filial hər iki sütundan birində olmalıdır (transfer üçün)
    private static String branchFilter(String columns) {
        if (!columns.contains("|")) return Session.branchFilter(columns);
        String ids = Session.scopeIds();
        String[] c = columns.split("\\|");
        return " AND (" + c[0] + " IN (" + ids + ") OR " + c[1] + " IN (" + ids + "))";
    }

    private void export() {
        ObservableList<Row> rows = table.getItems();
        if (rows == null || rows.isEmpty()) {
            Ui.warn("İxrac etmək üçün məlumat yoxdur.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Hesabatı saxla");
        chooser.setInitialFileName(typeBox.getValue().name().replaceAll("[^\\p{L}\\p{N}]+", "_") + "_" + LocalDate.now() + ".csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (Excel)", "*.csv"));
        File file = chooser.showSaveDialog(com.azerstar.util.Nav.stage());
        if (file == null) return;

        StringBuilder sb = new StringBuilder("﻿");
        List<String> keys = new ArrayList<>();
        for (String k : rows.get(0).columns()) keys.add(k);
        sb.append(String.join(";", keys.stream().map(ReportScreen::csv).toList())).append("\r\n");
        for (Row r : rows) {
            List<String> cells = new ArrayList<>();
            for (String k : keys) cells.add(csv(r.text(k)));
            sb.append(String.join(";", cells)).append("\r\n");
        }
        try {
            Files.writeString(file.toPath(), sb.toString(), StandardCharsets.UTF_8);
            Ui.info("Hesabat saxlanıldı:\n" + file.getAbsolutePath());
        } catch (IOException e) {
            Ui.error("Faylı yazmaq mümkün olmadı: " + e.getMessage());
        }
    }

    private static String csv(String s) {
        if (s == null) return "";
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
}
