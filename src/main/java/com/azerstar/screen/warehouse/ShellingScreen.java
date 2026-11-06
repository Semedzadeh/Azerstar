package com.azerstar.screen.warehouse;

import com.azerstar.screen.Menus;
import com.azerstar.util.Choice;
import com.azerstar.util.DataTable;
import com.azerstar.util.Db;
import com.azerstar.util.FormDialog;
import com.azerstar.util.ListScreen;
import com.azerstar.util.Row;
import com.azerstar.util.Session;
import com.azerstar.util.Ui;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

/** Qırma: qurudulmuş fındıqdan ləpə çıxarılması. */
public class ShellingScreen extends ListScreen {

    public ShellingScreen() {
        super("Qırma", Menus::warehouse, buildTable());
        init(Ui.primary("Yeni qırma", () -> {
            if (openNew(null)) reload();
        }));
    }

    private static TableView<Row> buildTable() {
        List<String> cols = new ArrayList<>(List.of("№", "shelling_id", "Tarix/saat", "date_time"));
        if (Session.hasManyBranches()) cols.addAll(List.of("Filial", "branch_name"));
        cols.addAll(List.of("Qurutma №", "drying_id", "Qurutma yeri", "place", "Qırmaya verilən (kq)", "input_kg",
                "Çıxan ləpə (kq)", "kernel_kg", "Ləpə çıxımı (%)", "yield_pct", "Qeyd", "note"));
        return DataTable.create(cols.toArray(new String[0]));
    }

    @Override
    protected ObservableList<Row> load() {
        return Db.query("SELECT s.*, b.name AS branch_name, o.drying_id, COALESCE(t.name, d.location) AS place, "
                + "COALESCE((SELECT SUM(k.weight_kg) FROM kernel_stock k WHERE k.shelling_id = s.shelling_id), 0) AS kernel_kg, "
                + "ROUND(COALESCE((SELECT SUM(k.weight_kg) FROM kernel_stock k WHERE k.shelling_id = s.shelling_id), 0) / s.input_kg * 100, 1) AS yield_pct "
                + "FROM shelling s JOIN branch b ON b.branch_id = s.branch_id JOIN drying_output o ON o.output_id = s.output_id "
                + "JOIN drying d ON d.drying_id = o.drying_id LEFT JOIN tank t ON t.tank_id = d.tank_id "
                + "WHERE 1 = 1" + Session.branchFilter("s.branch_id") + " ORDER BY s.date_time DESC, s.shelling_id DESC");
    }

    /** Yeni qırma pəncərəsi. preselectOutput verilərsə həmin mənbə seçilmiş gəlir. */
    static boolean openNew(Integer preselectOutput) {
        List<Row> outputs = Db.query("SELECT o.output_id, o.destination, o.remaining_kg, o.drying_id, COALESCE(t.name, d.location) AS place "
                + "FROM drying_output o JOIN drying d ON d.drying_id = o.drying_id LEFT JOIN tank t ON t.tank_id = d.tank_id "
                + "WHERE o.remaining_kg > 0.0001" + Session.branchFilter("o.branch_id") + " ORDER BY o.date_time DESC");
        if (outputs.isEmpty()) {
            Ui.warn("Qırmaya verilə biləcək fındıq yoxdur. Əvvəlcə qurutmanı bitirin.");
            return false;
        }
        FormDialog f = new FormDialog("Yeni qırma");
        List<Choice> choices = new ArrayList<>();
        Choice pre = null;
        for (Row o : outputs) {
            Choice c = new Choice(o.integer("output_id"), "Qurutma №" + o.integer("drying_id") + " · " + o.str("place")
                    + " · " + ("WAREHOUSE".equals(o.str("destination")) ? "anbarda" : "qırma gözləyir")
                    + " · qalıq: " + o.text("remaining_kg") + " kq");
            choices.add(c);
            if (preselectOutput != null && c.id() == preselectOutput) pre = c;
        }
        ComboBox<Choice> source = f.combo("Mənbə (qurudulmuş fındıq)", choices);
        source.setPrefWidth(400);
        if (pre != null) source.setValue(pre);
        TextField kg = f.number("Qırmaya verilən miqdar (kq)");
        TextField note = f.text("Qeyd");
        f.addInfo("Ləpə çıxandan sonra onu «Hazır ləpə» bölməsində əlavə edirsiniz.");

        return f.show("Qırmaya ver", () -> {
            if (source.getValue() == null) return "Mənbəni seçin.";
            Double w = Ui.number(kg);
            if (w == null || w <= 0) return "Miqdarı (kq) daxil edin.";
            int outputId = source.getValue().id();
            String[] error = {null};
            Db.tx(tx -> {
                Row o = tx.query("SELECT branch_id, remaining_kg FROM drying_output WHERE output_id = ?", outputId).get(0);
                if (w > o.number("remaining_kg") + 0.0001) {
                    error[0] = "Bu mənbədə yalnız " + Row.format(o.number("remaining_kg")) + " kq qalıb.";
                    return null;
                }
                tx.update("UPDATE drying_output SET remaining_kg = remaining_kg - ? WHERE output_id = ?", w, outputId);
                tx.insert("INSERT INTO shelling (branch_id, output_id, input_kg, note, created_by) VALUES (?, ?, ?, ?, ?)",
                        o.integer("branch_id"), outputId, w, note.getText().trim(), Session.getAccountId());
                return null;
            });
            return error[0];
        });
    }
}
