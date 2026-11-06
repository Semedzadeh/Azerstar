package com.azerstar.screen.analysis;

import com.azerstar.screen.Menus;
import com.azerstar.util.Choice;
import com.azerstar.util.DataTable;
import com.azerstar.util.Db;
import com.azerstar.util.Formulas;
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

/** Laboratoriya analizi: nümunə çəkiləri qramla, ləpə faizi proqramda hesablanır. */
public class AnalysisScreen extends ListScreen {

    public AnalysisScreen() {
        super("Analiz - Laboratoriya", Menus::main, buildTable());
        init(Ui.primary("Əlavə et", () -> openForm(null)),
                Ui.button("Düzəliş et", this::edit));
    }

    private static TableView<Row> buildTable() {
        List<String> cols = new ArrayList<>(List.of("Tarix/saat", "date_time"));
        if (Session.hasManyBranches()) cols.addAll(List.of("Filial", "branch_name"));
        cols.addAll(List.of(
                "Müştəri adı", "customer_name",
                "Məhsul miqdarı (kq)", "weight_kg",
                "Analiz üçün götürülmüş fındıq (q)", "sample_weight_g",
                "Ləpənin çəkisi (q)", "kernel_weight_g",
                "Fındıqda ləpə faizi (%)", "kernel_pct",
                "Mindal (q)", "mindal_g",
                "Çürük (q)", "rotten_g",
                "Ləpənin nəmliyi (%)", "kernel_moisture",
                "Qeyd", "note"));
        return DataTable.create(cols.toArray(new String[0]));
    }

    @Override
    protected ObservableList<Row> load() {
        return Db.query("SELECT a.*, " + Formulas.kernelPercentSql("a") + " AS kernel_pct, r.customer_name, r.weight_kg, b.name AS branch_name "
                + "FROM analysis a JOIN receipt r ON r.receipt_id = a.receipt_id JOIN branch b ON b.branch_id = a.branch_id "
                + "WHERE 1 = 1" + Session.branchFilter("a.branch_id") + " ORDER BY a.date_time DESC, a.analysis_id DESC");
    }

    private void edit() {
        Row r = selected();
        if (r != null) openForm(r);
    }

    private void openForm(Row existing) {
        FormDialog f = new FormDialog(existing == null ? "Yeni analiz" : "Analizə düzəliş");

        ComboBox<Choice> receipt = null;
        if (existing == null) {
            List<Choice> choices = new ArrayList<>();
            for (Row r : Db.query("SELECT receipt_id, customer_name, weight_kg, date_time FROM receipt r WHERE NOT EXISTS (SELECT 1 FROM analysis a WHERE a.receipt_id = r.receipt_id)"
                    + Session.branchFilter("r.branch_id") + " ORDER BY date_time DESC, receipt_id DESC")) {
                choices.add(new Choice(r.integer("receipt_id"), "№" + r.integer("receipt_id") + " · " + r.str("customer_name")
                        + " · " + r.text("weight_kg") + " kq · " + r.text("date_time")));
            }
            if (choices.isEmpty()) {
                Ui.warn("Analiz edilməmiş mal qəbulu yoxdur. Əvvəlcə yeni mal qəbulu əlavə edin.");
                return;
            }
            receipt = f.combo("Mal qəbulu", choices);
            receipt.setPrefWidth(380);
        } else {
            f.addInfo("Müştəri: " + existing.str("customer_name") + " · miqdar: " + existing.text("weight_kg") + " kq");
        }

        TextField sample = f.number("Götürülmüş fındıq çəkisi (q)");
        TextField kernel = f.number("Ləpənin çəkisi (q)");
        TextField mindal = f.number("Mindal (q)");
        TextField rotten = f.number("Çürük (q)");
        TextField moisture = f.number("Ləpənin nəmliyi (%)");
        TextField note = f.text("Qeyd");

        if (existing != null) {
            sample.setText(existing.text("sample_weight_g"));
            kernel.setText(existing.text("kernel_weight_g"));
            mindal.setText(existing.text("mindal_g"));
            rotten.setText(existing.text("rotten_g"));
            moisture.setText(existing.text("kernel_moisture"));
            note.setText(existing.str("note"));
        }

        final ComboBox<Choice> receiptBox = receipt;
        boolean saved = f.show("Saxla", () -> {
            Double s = Ui.number(sample);
            if (existing == null && receiptBox.getValue() == null) return "Mal qəbulunu seçin.";
            if (s == null || s <= 0) return "Götürülmüş fındıq çəkisini (q) daxil edin.";
            double k = nz(Ui.number(kernel));
            double m = nz(Ui.number(mindal));
            double c = nz(Ui.number(rotten));
            if (k > s || m > s || c > s) return "Ləpə, mindal və çürük çəkisi götürülmüş nümunədən böyük ola bilməz.";
            Double wet = Ui.number(moisture);
            String n = note.getText().trim();
            if (existing == null) {
                Row taken = Db.queryOne("SELECT COUNT(*) AS n FROM analysis WHERE receipt_id = ?", receiptBox.getValue().id());
                if (taken.integer("n") > 0) return "Bu mal qəbulu artıq analiz edilib. Düzəliş etmək üçün siyahıdan seçin.";
                Db.insert("INSERT INTO analysis (receipt_id, branch_id, sample_weight_g, kernel_weight_g, mindal_g, rotten_g, kernel_moisture, note, created_by) "
                                + "SELECT receipt_id, branch_id, ?, ?, ?, ?, ?, ?, ? FROM receipt WHERE receipt_id = ?",
                        s, k, m, c, wet, n, Session.getAccountId(), receiptBox.getValue().id());
            } else {
                Db.update("UPDATE analysis SET sample_weight_g = ?, kernel_weight_g = ?, mindal_g = ?, rotten_g = ?, kernel_moisture = ?, note = ? WHERE analysis_id = ?",
                        s, k, m, c, wet, n, existing.integer("analysis_id"));
            }
            return null;
        });
        if (saved) reload();
    }

    private static double nz(Double d) {
        return d == null ? 0 : d;
    }
}
