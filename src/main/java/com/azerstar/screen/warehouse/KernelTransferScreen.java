package com.azerstar.screen.warehouse;

import com.azerstar.screen.Menus;
import com.azerstar.util.DataTable;
import com.azerstar.util.Db;
import com.azerstar.util.ListScreen;
import com.azerstar.util.Row;
import com.azerstar.util.Session;
import com.azerstar.util.Ui;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

/** Filiallar arası ləpə transferi: göndərilənlər və təsdiq gözləyənlər. */
public class KernelTransferScreen extends ListScreen {

    public KernelTransferScreen() {
        super("Transfer edilmiş ləpə", Menus::warehouse, buildTable());
        init(Ui.primary("Qəbul et", this::receive),
                Ui.danger("Ləğv et", this::cancel));
    }

    private static TableView<Row> buildTable() {
        return DataTable.create("№", "transfer_id", "Göndərilmə", "sent_time", "Haradan", "from_name", "Hara", "to_name",
                "Ləpə növü", "kernel_type", "Ləpə ölçüsü", "kernel_size", "Miqdar (kq)", "weight_kg",
                "Vəziyyət", "status_text", "Qəbul tarixi", "received_time", "Qeyd", "note");
    }

    @Override
    protected ObservableList<Row> load() {
        String ids = Session.scopeIds();
        String filter = " AND (t.from_branch_id IN (" + ids + ") OR t.to_branch_id IN (" + ids + "))";
        return Db.query("SELECT t.*, fb.name AS from_name, tb.name AS to_name, k.kernel_type, k.kernel_size, "
                + "CASE t.status WHEN 'SENT' THEN 'Təsdiq gözləyir' WHEN 'RECEIVED' THEN 'Qəbul edilib' ELSE 'Ləğv edilib' END AS status_text "
                + "FROM kernel_transfer t JOIN branch fb ON fb.branch_id = t.from_branch_id JOIN branch tb ON tb.branch_id = t.to_branch_id "
                + "JOIN kernel_stock k ON k.kernel_id = t.kernel_id WHERE 1 = 1" + filter
                + " ORDER BY t.sent_time DESC, t.transfer_id DESC");
    }

    private void receive() {
        Row t = selected();
        if (t == null) return;
        if (!"SENT".equals(t.str("status"))) {
            Ui.warn("Bu transfer artıq həll olunub.");
            return;
        }
        if (!Session.canAccessBranch(t.integer("to_branch_id"))) {
            Ui.warn("Transferi yalnız qəbul edən filial (" + t.str("to_name") + ") təsdiqləyə bilər.");
            return;
        }
        if (!Ui.confirm(t.text("weight_kg") + " kq ləpə qəbul edilsin və " + t.str("to_name") + " anbarına əlavə olunsun?")) return;
        int id = t.integer("transfer_id");
        boolean ok = Ui.run(() -> Db.tx(tx -> {
            Row cur = tx.query("SELECT * FROM kernel_transfer WHERE transfer_id = ?", id).get(0);
            if (!"SENT".equals(cur.str("status"))) return null;
            tx.update("UPDATE kernel_transfer SET status = 'RECEIVED', received_time = NOW(), received_by = ? WHERE transfer_id = ?",
                    Session.getAccountId(), id);
            tx.insert("INSERT INTO kernel_stock (branch_id, parent_kernel_id, kernel_type, kernel_size, weight_kg, remaining_kg, moisture, note, created_by) "
                            + "SELECT ?, kernel_id, kernel_type, kernel_size, ?, ?, moisture, ?, ? FROM kernel_stock WHERE kernel_id = ?",
                    cur.integer("to_branch_id"), cur.number("weight_kg"), cur.number("weight_kg"),
                    "Transfer №" + id, Session.getAccountId(), cur.integer("kernel_id"));
            return null;
        }));
        if (ok) reload();
    }

    private void cancel() {
        Row t = selected();
        if (t == null) return;
        if (!"SENT".equals(t.str("status"))) {
            Ui.warn("Yalnız təsdiq gözləyən transferi ləğv etmək olar.");
            return;
        }
        if (!Session.canAccessBranch(t.integer("from_branch_id"))) {
            Ui.warn("Transferi yalnız göndərən filial (" + t.str("from_name") + ") ləğv edə bilər.");
            return;
        }
        if (!Ui.confirm("Transfer ləğv edilsin? Ləpə göndərən filialın anbarına qaytarılacaq.")) return;
        int id = t.integer("transfer_id");
        boolean ok = Ui.run(() -> Db.tx(tx -> {
            Row cur = tx.query("SELECT * FROM kernel_transfer WHERE transfer_id = ?", id).get(0);
            if (!"SENT".equals(cur.str("status"))) return null;
            tx.update("UPDATE kernel_transfer SET status = 'CANCELLED' WHERE transfer_id = ?", id);
            tx.update("UPDATE kernel_stock SET remaining_kg = remaining_kg + ? WHERE kernel_id = ?",
                    cur.number("weight_kg"), cur.integer("kernel_id"));
            return null;
        }));
        if (ok) reload();
    }
}
