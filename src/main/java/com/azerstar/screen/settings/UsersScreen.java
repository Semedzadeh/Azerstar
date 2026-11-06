package com.azerstar.screen.settings;

import com.azerstar.screen.Menus;
import com.azerstar.util.Choice;
import com.azerstar.util.DataTable;
import com.azerstar.util.Db;
import com.azerstar.util.FormDialog;
import com.azerstar.util.ListScreen;
import com.azerstar.util.Row;
import com.azerstar.util.Section;
import com.azerstar.util.Session;
import com.azerstar.util.Ui;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Sadə istifadəçilər: görə biləcəyi filiallar və bölmə icazələri.
 * Baş admin hamısını idarə edir, filial admini yalnız öz filiallarındakıları.
 */
public class UsersScreen extends ListScreen {

    public UsersScreen() {
        super("İstifadəçilər və icazələr", Menus::branches,
                DataTable.create("Ad", "firstname", "Soyad", "lastname", "İstifadəçi adı", "username",
                        "Filiallar", "branches", "Bölmələr", "perms"));
        init(Ui.primary("Əlavə et", () -> openForm(null)),
                Ui.button("Düzəliş et", this::edit),
                Ui.danger("Sil", this::delete));
    }

    @Override
    protected ObservableList<Row> load() {
        // seçilmiş filialın istifadəçiləri; baş admin "Hamısı" seçibsə filialsız olanlar da daxil hamısı
        String scope = Session.isSuperAdmin() && Session.getViewBranchId() == 0 ? ""
                : " AND EXISTS (SELECT 1 FROM user_branch ub WHERE ub.account_id = u.account_id AND ub.branch_id IN (" + Session.scopeIds() + "))";
        ObservableList<Row> users = Db.query("SELECT u.* FROM user_account u WHERE u.role = 'USER'" + scope + " ORDER BY u.account_id");

        Map<Integer, List<String>> perms = new HashMap<>();
        for (Row p : Db.query("SELECT account_id, section FROM user_permission")) {
            try {
                String title = Section.valueOf(p.str("section")).title();
                perms.computeIfAbsent(p.integer("account_id"), k -> new ArrayList<>()).add(title);
            } catch (IllegalArgumentException ignored) {
                // naməlum bölmə adı varsa keç
            }
        }
        Map<Integer, List<String>> branches = new HashMap<>();
        for (Row b : Db.query("SELECT ub.account_id, br.name FROM user_branch ub JOIN branch br ON br.branch_id = ub.branch_id ORDER BY br.branch_id")) {
            branches.computeIfAbsent(b.integer("account_id"), k -> new ArrayList<>()).add(b.str("name"));
        }
        for (Row u : users) {
            int id = u.integer("account_id");
            u.put("perms", String.join(", ", perms.getOrDefault(id, List.of())));
            u.put("branches", String.join(", ", branches.getOrDefault(id, List.of())));
        }
        return users;
    }

    /** Filial admini yalnız bütün filialları onun filiallarının daxilində olan istifadəçiyə toxuna bilər. */
    private static boolean canManage(Row user) {
        if (Session.isSuperAdmin()) return true;
        Row outside = Db.queryOne("SELECT COUNT(*) AS n FROM user_branch WHERE account_id = ? AND branch_id NOT IN (" + Session.allowedIds() + ")",
                user.integer("account_id"));
        if (outside.integer("n") > 0) {
            Ui.warn("Bu istifadəçi sizin filialınızdan kənar filialda da işləyir. Onu yalnız baş admin dəyişə bilər.");
            return false;
        }
        return true;
    }

    private void edit() {
        Row r = selected();
        if (r != null && canManage(r)) openForm(r);
    }

    private void delete() {
        Row r = selected();
        if (r == null || !canManage(r)) return;
        if (!Ui.confirm(r.str("username") + " istifadəçisi silinsin?")) return;
        int id = r.integer("account_id");
        if (Ui.run(() -> Db.update("DELETE FROM user_account WHERE account_id = ? AND role = 'USER'", id))) reload();
    }

    private void openForm(Row existing) {
        FormDialog f = new FormDialog(existing == null ? "Yeni istifadəçi" : "İstifadəçiyə düzəliş");
        TextField first = f.text("Ad");
        TextField last = f.text("Soyad");
        TextField username = f.text("İstifadəçi adı");
        PasswordField password = f.password(existing == null ? "Şifrə" : "Yeni şifrə (boş = dəyişmir)");

        // yalnız verən şəxsin özünün görə bildiyi filiallar təklif olunur
        Map<Integer, CheckBox> branchBoxes = new LinkedHashMap<>();
        for (Choice b : Session.getAllowedBranches()) {
            branchBoxes.put(b.id(), f.check("Filial: " + b.label(), false));
        }
        Map<Section, CheckBox> boxes = new EnumMap<>(Section.class);
        for (Section s : Section.values()) {
            boxes.put(s, f.check("Bölmə: " + s.title(), false));
        }

        if (existing == null) {
            CheckBox current = branchBoxes.get(Session.getBranchId());
            if (current != null) current.setSelected(true);
        } else {
            first.setText(existing.str("firstname"));
            last.setText(existing.str("lastname"));
            username.setText(existing.str("username"));
            Set<String> have = new HashSet<>();
            for (Row p : Db.query("SELECT section FROM user_permission WHERE account_id = ?", existing.integer("account_id"))) {
                have.add(p.str("section"));
            }
            boxes.forEach((s, b) -> b.setSelected(have.contains(s.name())));
            for (Row b : Db.query("SELECT branch_id FROM user_branch WHERE account_id = ?", existing.integer("account_id"))) {
                CheckBox box = branchBoxes.get(b.integer("branch_id"));
                if (box != null) box.setSelected(true);
            }
        }

        boolean saved = f.show("Saxla", () -> {
            if (first.getText().isBlank() || last.getText().isBlank()) return "Ad və soyadı daxil edin.";
            if (username.getText().isBlank()) return "İstifadəçi adını daxil edin.";
            if (existing == null && password.getText().isBlank()) return "Şifrəni daxil edin.";
            if (branchBoxes.values().stream().noneMatch(CheckBox::isSelected)) return "Ən azı bir filial seçin.";
            if (boxes.values().stream().noneMatch(CheckBox::isSelected)) return "Ən azı bir bölmə seçin.";
            // əsas filial: seçilənlərin ilkidir
            int home = 0;
            for (Map.Entry<Integer, CheckBox> e : branchBoxes.entrySet()) {
                if (e.getValue().isSelected()) {
                    home = e.getKey();
                    break;
                }
            }
            final int homeBranch = home;
            Db.tx(tx -> {
                long id;
                if (existing == null) {
                    id = tx.insert("INSERT INTO user_account (firstname, lastname, username, password, branch_id, role) VALUES (?, ?, ?, ?, ?, 'USER')",
                            first.getText().trim(), last.getText().trim(), username.getText().trim(), password.getText(), homeBranch);
                } else {
                    id = existing.integer("account_id");
                    tx.update("UPDATE user_account SET firstname = ?, lastname = ?, username = ?, branch_id = ? WHERE account_id = ? AND role = 'USER'",
                            first.getText().trim(), last.getText().trim(), username.getText().trim(), homeBranch, id);
                    if (!password.getText().isBlank()) {
                        tx.update("UPDATE user_account SET password = ? WHERE account_id = ?", password.getText(), id);
                    }
                    tx.update("DELETE FROM user_permission WHERE account_id = ?", id);
                    tx.update("DELETE FROM user_branch WHERE account_id = ?", id);
                }
                for (Map.Entry<Integer, CheckBox> e : branchBoxes.entrySet()) {
                    if (e.getValue().isSelected()) {
                        tx.update("INSERT INTO user_branch (account_id, branch_id) VALUES (?, ?)", id, e.getKey());
                    }
                }
                for (Map.Entry<Section, CheckBox> e : boxes.entrySet()) {
                    if (e.getValue().isSelected()) {
                        tx.update("INSERT INTO user_permission (account_id, section) VALUES (?, ?)", id, e.getKey().name());
                    }
                }
                return null;
            });
            return null;
        });
        if (saved) reload();
    }
}
