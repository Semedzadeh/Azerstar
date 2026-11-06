package com.azerstar.screen.receipt;

import com.azerstar.screen.Menus;
import com.azerstar.util.DataTable;
import com.azerstar.util.Db;
import com.azerstar.util.FormDialog;
import com.azerstar.util.ListScreen;
import com.azerstar.util.Row;
import com.azerstar.util.Session;
import com.azerstar.util.Ui;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/** Pərakəndə və ya topdan mal qəbulu siyahısı. */
public class ReceiptScreen extends ListScreen {

    private final String type;

    public ReceiptScreen(String type) {
        super("RETAIL".equals(type) ? "Pərakəndə mal qəbulu" : "Topdan mal qəbulu", Menus::receipt, buildTable());
        this.type = type;
        init(Ui.primary("Əlavə et", () -> openForm(null)),
                Ui.button("Düzəliş et", this::edit));
    }

    private static TableView<Row> buildTable() {
        if (Session.hasManyBranches()) {
            return DataTable.create("Tarix/saat", "date_time", "Filial", "branch_name", "Müştəri adı", "customer_name",
                    "Miqdar (kq)", "weight_kg", "Qiymət (1 kq / AZN)", "unit_price", "Cəm məbləğ (AZN)", "total_amount", "Qeyd", "note");
        }
        return DataTable.create("Tarix/saat", "date_time", "Müştəri adı", "customer_name",
                "Miqdar (kq)", "weight_kg", "Qiymət (1 kq / AZN)", "unit_price", "Cəm məbləğ (AZN)", "total_amount", "Qeyd", "note");
    }

    @Override
    protected ObservableList<Row> load() {
        return Db.query("SELECT r.*, b.name AS branch_name FROM receipt r JOIN branch b ON b.branch_id = r.branch_id "
                + "WHERE r.receipt_type = ?" + Session.branchFilter("r.branch_id") + " ORDER BY r.date_time DESC, r.receipt_id DESC", type);
    }

    private void edit() {
        Row r = selected();
        if (r != null) openForm(r);
    }

    private void openForm(Row existing) {
        FormDialog f = new FormDialog(existing == null ? "Yeni mal qəbulu" : "Mal qəbuluna düzəliş");
        TextField customer = f.text("Müştəri adı");
        TextField weight = f.number("Miqdar (kq)");
        TextField price = f.number("Qiymət (1 kq / AZN)");
        TextField total = f.number("Cəm məbləğ (AZN)");
        TextField note = f.text("Qeyd");

        if (existing != null) {
            customer.setText(existing.str("customer_name"));
            weight.setText(existing.text("weight_kg"));
            price.setText(existing.text("unit_price"));
            total.setText(existing.text("total_amount"));
            note.setText(existing.str("note"));
        }

        // miqdar və ya qiymət dəyişəndə cəm məbləğ özü hesablanır (istəsəniz əl ilə də dəyişə bilərsiniz)
        Runnable recalc = () -> {
            Double w = Ui.number(weight);
            Double p = Ui.number(price);
            if (w != null && p != null) total.setText(Row.format(w * p));
        };
        weight.textProperty().addListener((o, a, b) -> recalc.run());
        price.textProperty().addListener((o, a, b) -> recalc.run());

        boolean saved = f.show("Saxla", () -> {
            String name = customer.getText().trim();
            Double w = Ui.number(weight);
            if (name.isEmpty()) return "Müştəri adını daxil edin.";
            if (w == null || w <= 0) return "Miqdarı (kq) düzgün daxil edin.";
            double p = nz(Ui.number(price));
            double t = nz(Ui.number(total));
            if (existing == null) {
                Db.insert("INSERT INTO receipt (branch_id, receipt_type, customer_name, weight_kg, unit_price, total_amount, note, created_by) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        Session.getBranchId(), type, name, w, p, t, note.getText().trim(), Session.getAccountId());
            } else {
                Row used = Db.queryOne("SELECT COALESCE(SUM(weight_kg), 0) AS s FROM drying_source WHERE receipt_id = ?", existing.integer("receipt_id"));
                if (w < used.number("s")) {
                    return "Bu qəbuldan " + Row.format(used.number("s")) + " kq qurutmaya verilib, miqdar bundan az ola bilməz.";
                }
                Db.update("UPDATE receipt SET customer_name = ?, weight_kg = ?, unit_price = ?, total_amount = ?, note = ? WHERE receipt_id = ?",
                        name, w, p, t, note.getText().trim(), existing.integer("receipt_id"));
            }
            return null;
        });
        if (saved) reload();
    }

    private static double nz(Double d) {
        return d == null ? 0 : d;
    }
}
