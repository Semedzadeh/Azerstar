package com.azerstar.screen.settings;

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
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

/** Qurutma çənlərinin idarəsi: artırmaq, azaltmaq, passiv etmək. */
public class TanksScreen extends ListScreen {

    public TanksScreen() {
        super("Qurutma çənləri", Menus::branches,
                DataTable.create("Çən", "name", "Filial", "branch_name", "Tutum (kq)", "capacity_kg",
                        "Vəziyyət", "active_text", "Hazırda", "busy_text"));
        init(Ui.primary("Əlavə et", () -> openForm(null)),
                Ui.button("Düzəliş et", this::edit),
                Ui.danger("Sil", this::delete));
    }

    @Override
    protected ObservableList<Row> load() {
        return Db.query("SELECT t.*, b.name AS branch_name, IF(t.active = 1, 'Aktiv', 'Passiv') AS active_text, "
                + "IF(EXISTS (SELECT 1 FROM drying d WHERE d.tank_id = t.tank_id AND d.status = 'ACTIVE'), 'Dolu', 'Boş') AS busy_text "
                + "FROM tank t JOIN branch b ON b.branch_id = t.branch_id WHERE 1 = 1" + Session.branchFilter("t.branch_id")
                + " ORDER BY t.branch_id, t.tank_id");
    }

    private void edit() {
        Row r = selected();
        if (r != null) openForm(r);
    }

    private void delete() {
        Row r = selected();
        if (r == null) return;
        if (!Ui.confirm(r.str("name") + " çəni silinsin?")) return;
        try {
            Db.update("DELETE FROM tank WHERE tank_id = ?", r.integer("tank_id"));
            reload();
        } catch (Db.DbException e) {
            Ui.error("Bu çəndə qurutma qeydləri var, silmək olmur. Əvəzinə düzəliş edib «Aktiv» işarəsini götürün.");
        }
    }

    private void openForm(Row existing) {
        FormDialog f = new FormDialog(existing == null ? "Yeni çən" : "Çənə düzəliş");
        TextField name = f.text("Çənin adı");
        TextField capacity = f.number("Tutum (kq, məcburi deyil)");
        ComboBox<Choice> branch = null;
        if (Session.hasManyBranches()) {
            List<Choice> branches = Session.getAllowedBranches();
            branch = f.combo("Filial", branches);
            int current = existing == null ? Session.getBranchId() : existing.integer("branch_id");
            for (Choice c : branches) if (c.id() == current) branch.setValue(c);
        }
        CheckBox active = f.check("Aktiv", true);

        if (existing != null) {
            name.setText(existing.str("name"));
            capacity.setText(existing.text("capacity_kg"));
            active.setSelected(existing.integer("active") == 1);
        }

        final ComboBox<Choice> branchBox = branch;
        boolean saved = f.show("Saxla", () -> {
            if (name.getText().isBlank()) return "Çənin adını yazın.";
            int branchId = branchBox != null ? branchBox.getValue().id()
                    : existing != null ? existing.integer("branch_id") : Session.getBranchId();
            Double cap = Ui.number(capacity);
            if (existing == null) {
                Db.insert("INSERT INTO tank (branch_id, name, capacity_kg, active) VALUES (?, ?, ?, ?)",
                        branchId, name.getText().trim(), cap, active.isSelected() ? 1 : 0);
            } else {
                if (!active.isSelected()) {
                    Row busy = Db.queryOne("SELECT COUNT(*) AS n FROM drying WHERE tank_id = ? AND status = 'ACTIVE'", existing.integer("tank_id"));
                    if (busy.integer("n") > 0) return "Çəndə qurutma gedir, əvvəlcə onu bitirin.";
                }
                Db.update("UPDATE tank SET name = ?, branch_id = ?, capacity_kg = ?, active = ? WHERE tank_id = ?",
                        name.getText().trim(), branchId, cap, active.isSelected() ? 1 : 0, existing.integer("tank_id"));
            }
            return null;
        });
        if (saved) reload();
    }
}
