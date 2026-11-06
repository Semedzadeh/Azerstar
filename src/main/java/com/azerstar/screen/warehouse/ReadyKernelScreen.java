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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

/** Hazır ləpə anbarı: əlavə etmək, transfer etmək, satmaq. */
public class ReadyKernelScreen extends ListScreen {

    private final CheckBox showEmpty = new CheckBox("Tükənmişləri də göstər");

    public ReadyKernelScreen() {
        super("Hazır ləpə", Menus::warehouse, buildTable());
        showEmpty.setOnAction(e -> reload());
        init(Ui.primary("Əlavə et", () -> {
                    if (openAdd()) reload();
                }),
                Ui.button("Transfer et", this::transfer),
                Ui.button("Sat", this::sell),
                showEmpty);
    }

    private static TableView<Row> buildTable() {
        List<String> cols = new ArrayList<>(List.of("S/N", "kernel_id", "Tarix/saat", "date_time"));
        if (Session.hasManyBranches()) cols.addAll(List.of("Filial", "branch_name"));
        cols.addAll(List.of("Ləpə növü", "kernel_type", "Ləpə ölçüsü", "kernel_size", "Qalıq (kq)", "remaining_kg",
                "İlkin (kq)", "weight_kg", "Nəmlik (%)", "moisture", "Mənbə", "source_text", "Qeyd", "note"));
        return DataTable.create(cols.toArray(new String[0]));
    }

    @Override
    protected ObservableList<Row> load() {
        return Db.query("SELECT k.*, b.name AS branch_name, "
                + "CASE WHEN k.shelling_id IS NOT NULL THEN CONCAT('Qırma №', k.shelling_id) "
                + "WHEN k.parent_kernel_id IS NOT NULL THEN CONCAT('Transfer (S/N ', k.parent_kernel_id, ')') ELSE 'Əl ilə' END AS source_text "
                + "FROM kernel_stock k JOIN branch b ON b.branch_id = k.branch_id WHERE 1 = 1"
                + (showEmpty.isSelected() ? "" : " AND k.remaining_kg > 0.0001")
                + Session.branchFilter("k.branch_id") + " ORDER BY k.date_time DESC, k.kernel_id DESC");
    }

    private static List<String> distinct(String column) {
        List<String> list = new ArrayList<>();
        for (Row r : Db.query("SELECT DISTINCT " + column + " AS v FROM kernel_stock WHERE " + column + " IS NOT NULL AND " + column + " <> '' ORDER BY v")) {
            list.add(r.str("v"));
        }
        return list;
    }

    private boolean openAdd() {
        FormDialog f = new FormDialog("Hazır ləpə əlavə et");
        List<Choice> shellings = new ArrayList<>();
        for (Row s : Db.query("SELECT s.shelling_id, s.input_kg, s.date_time, COALESCE(t.name, d.location) AS place "
                + "FROM shelling s JOIN drying_output o ON o.output_id = s.output_id JOIN drying d ON d.drying_id = o.drying_id "
                + "LEFT JOIN tank t ON t.tank_id = d.tank_id WHERE 1 = 1" + Session.branchFilter("s.branch_id")
                + " ORDER BY s.date_time DESC, s.shelling_id DESC")) {
            shellings.add(new Choice(s.integer("shelling_id"), "Qırma №" + s.integer("shelling_id") + " · " + s.str("place")
                    + " · " + s.text("input_kg") + " kq · " + s.text("date_time")));
        }
        ComboBox<Choice> shelling = f.combo("Hansı qırmadan (məcburi deyil)", shellings);
        shelling.setPrefWidth(400);
        ComboBox<String> type = f.editableCombo("Ləpə növü", distinct("kernel_type"));
        ComboBox<String> size = f.editableCombo("Ləpə ölçüsü", distinct("kernel_size"));
        TextField kg = f.number("Miqdar (kq)");
        TextField moisture = f.number("Nəmlik (%)");
        TextField note = f.text("Qeyd");

        return f.show("Əlavə et", () -> {
            Double w = Ui.number(kg);
            if (w == null || w <= 0) return "Miqdarı (kq) daxil edin.";
            Integer shellingId = shelling.getValue() == null ? null : shelling.getValue().id();
            String t = type.getEditor().getText().trim();
            String s = size.getEditor().getText().trim();
            Db.insert("INSERT INTO kernel_stock (branch_id, shelling_id, kernel_type, kernel_size, weight_kg, remaining_kg, moisture, note, created_by) "
                            + "VALUES (COALESCE((SELECT branch_id FROM shelling WHERE shelling_id = ?), ?), ?, ?, ?, ?, ?, ?, ?, ?)",
                    shellingId, Session.getBranchId(), shellingId, t, s, w, w, Ui.number(moisture), note.getText().trim(), Session.getAccountId());
            return null;
        });
    }

    private void transfer() {
        Row k = selected();
        if (k == null) return;
        if (k.number("remaining_kg") <= 0.0001) {
            Ui.warn("Bu ləpədə qalıq yoxdur.");
            return;
        }
        int from = k.integer("branch_id");
        List<Choice> targets = new ArrayList<>();
        for (Row b : Db.query("SELECT branch_id, name FROM branch WHERE branch_id <> ? ORDER BY branch_id", from)) {
            targets.add(new Choice(b.integer("branch_id"), b.str("name")));
        }
        FormDialog f = new FormDialog("Ləpəni transfer et");
        f.addInfo("S/N " + k.integer("kernel_id") + " · " + nz(k.str("kernel_type")) + " · " + nz(k.str("kernel_size"))
                + " · qalıq: " + k.text("remaining_kg") + " kq · filial: " + k.str("branch_name"));
        ComboBox<Choice> to = f.combo("Hansı filiala", targets);
        if (targets.size() == 1) to.setValue(targets.get(0));
        TextField kg = f.number("Miqdar (kq)");
        kg.setText(k.text("remaining_kg"));
        TextField note = f.text("Qeyd");
        f.addInfo("Qəbul edən filial təsdiqləyənə qədər ləpə onun anbarına düşmür.");

        boolean saved = f.show("Göndər", () -> {
            if (to.getValue() == null) return "Filialı seçin.";
            Double w = Ui.number(kg);
            if (w == null || w <= 0) return "Miqdarı (kq) daxil edin.";
            int kernelId = k.integer("kernel_id");
            String[] error = {null};
            Db.tx(tx -> {
                Row cur = tx.query("SELECT remaining_kg FROM kernel_stock WHERE kernel_id = ?", kernelId).get(0);
                if (w > cur.number("remaining_kg") + 0.0001) {
                    error[0] = "Anbarda yalnız " + Row.format(cur.number("remaining_kg")) + " kq qalıb.";
                    return null;
                }
                tx.update("UPDATE kernel_stock SET remaining_kg = remaining_kg - ? WHERE kernel_id = ?", w, kernelId);
                tx.insert("INSERT INTO kernel_transfer (kernel_id, from_branch_id, to_branch_id, weight_kg, sent_by, note) VALUES (?, ?, ?, ?, ?, ?)",
                        kernelId, from, to.getValue().id(), w, Session.getAccountId(), note.getText().trim());
                return null;
            });
            return error[0];
        });
        if (saved) reload();
    }

    private void sell() {
        Row k = selected();
        if (k == null) return;
        if (k.number("remaining_kg") <= 0.0001) {
            Ui.warn("Bu ləpədə qalıq yoxdur.");
            return;
        }
        FormDialog f = new FormDialog("Ləpə satışı");
        f.addInfo("S/N " + k.integer("kernel_id") + " · " + nz(k.str("kernel_type")) + " · " + nz(k.str("kernel_size"))
                + " · qalıq: " + k.text("remaining_kg") + " kq");
        ComboBox<Choice> type = f.combo("Satış növü", List.of(new Choice(1, "Pərakəndə"), new Choice(2, "Topdan")));
        type.setValue(type.getItems().get(0));
        TextField customer = f.text("Müştəri adı");
        TextField kg = f.number("Miqdar (kq)");
        TextField price = f.number("Qiymət (1 kq / AZN)");
        TextField total = f.number("Cəm məbləğ (AZN)");
        TextField note = f.text("Qeyd");
        Runnable recalc = () -> {
            Double w = Ui.number(kg);
            Double p = Ui.number(price);
            if (w != null && p != null) total.setText(Row.format(w * p));
        };
        kg.textProperty().addListener((o, a, b) -> recalc.run());
        price.textProperty().addListener((o, a, b) -> recalc.run());

        boolean saved = f.show("Sat", () -> {
            if (customer.getText().isBlank()) return "Müştəri adını daxil edin.";
            Double w = Ui.number(kg);
            if (w == null || w <= 0) return "Miqdarı (kq) daxil edin.";
            double p = nz(Ui.number(price));
            double t = nz(Ui.number(total));
            int kernelId = k.integer("kernel_id");
            String[] error = {null};
            Db.tx(tx -> {
                Row cur = tx.query("SELECT branch_id, remaining_kg FROM kernel_stock WHERE kernel_id = ?", kernelId).get(0);
                if (w > cur.number("remaining_kg") + 0.0001) {
                    error[0] = "Anbarda yalnız " + Row.format(cur.number("remaining_kg")) + " kq qalıb.";
                    return null;
                }
                tx.update("UPDATE kernel_stock SET remaining_kg = remaining_kg - ? WHERE kernel_id = ?", w, kernelId);
                tx.insert("INSERT INTO kernel_sale (kernel_id, branch_id, sale_type, customer_name, weight_kg, unit_price, total_amount, note, created_by) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        kernelId, cur.integer("branch_id"), type.getValue().id() == 1 ? "RETAIL" : "WHOLESALE",
                        customer.getText().trim(), w, p, t, note.getText().trim(), Session.getAccountId());
                return null;
            });
            return error[0];
        });
        if (saved) reload();
    }

    private static String nz(String s) {
        return s == null || s.isBlank() ? "-" : s;
    }

    private static double nz(Double d) {
        return d == null ? 0 : d;
    }
}
