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
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

/** Filialların siyahısı (yalnız baş admin): əlavə et, adını dəyiş, istifadə olunmayanı sil. */
public class BranchesScreen extends ListScreen {

    public BranchesScreen() {
        super("Filiallar", Menus::settings,
                DataTable.create("№", "branch_id", "Filial", "name", "İstifadəçi", "user_count", "Çən", "tank_count", "Mal qəbulu", "receipt_count"),
                false);
        init(Ui.primary("Filial əlavə et", () -> openForm(null)),
                Ui.button("Adını dəyiş", this::edit),
                Ui.danger("Sil", this::delete));
    }

    @Override
    protected ObservableList<Row> load() {
        return Db.query("SELECT b.branch_id, b.name, "
                + "(SELECT COUNT(*) FROM user_branch ub WHERE ub.branch_id = b.branch_id) AS user_count, "
                + "(SELECT COUNT(*) FROM tank t WHERE t.branch_id = b.branch_id) AS tank_count, "
                + "(SELECT COUNT(*) FROM receipt r WHERE r.branch_id = b.branch_id) AS receipt_count "
                + "FROM branch b ORDER BY b.branch_id");
    }

    private void edit() {
        Row r = selected();
        if (r != null) openForm(r);
    }

    private void delete() {
        Row r = selected();
        if (r == null) return;
        if (Db.queryOne("SELECT COUNT(*) AS n FROM branch").integer("n") <= 1) {
            Ui.warn("Son filialı silmək olmaz.");
            return;
        }
        if (!Ui.confirm(r.str("name") + " filialı silinsin?")) return;
        try {
            Db.update("DELETE FROM branch WHERE branch_id = ?", r.integer("branch_id"));
            refreshSession();
            reload();
        } catch (Db.DbException e) {
            Ui.error("Bu filialın istifadəçiləri, çənləri və ya qeydləri var, silmək olmur. Əvvəl onları başqa filiala keçirin və ya silin.");
        }
    }

    private void openForm(Row existing) {
        FormDialog f = new FormDialog(existing == null ? "Yeni filial" : "Filialın adı");
        TextField name = f.text("Filialın adı");
        if (existing != null) name.setText(existing.str("name"));
        boolean saved = f.show("Saxla", () -> {
            String n = name.getText().trim();
            if (n.isEmpty()) return "Filialın adını yazın.";
            if (existing == null) {
                Db.insert("INSERT INTO branch (branch_id, name) SELECT COALESCE(MAX(branch_id), 0) + 1, ? FROM branch", n);
            } else {
                Db.update("UPDATE branch SET name = ? WHERE branch_id = ?", n, existing.integer("branch_id"));
            }
            return null;
        });
        if (saved) {
            refreshSession();
            reload();
        }
    }

    /** Baş admin bütün filialları görür; siyahı dəyişdisə cari sessiyada da yenilənir. */
    private static void refreshSession() {
        if (!Session.isSuperAdmin()) return;
        List<Choice> all = new ArrayList<>();
        for (Row b : Db.query("SELECT branch_id, name FROM branch ORDER BY branch_id")) {
            all.add(new Choice(b.integer("branch_id"), b.str("name")));
        }
        Session.setAllowedBranches(all);
    }
}
