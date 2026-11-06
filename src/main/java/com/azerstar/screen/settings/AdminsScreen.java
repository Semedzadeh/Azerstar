package com.azerstar.screen.settings;

import com.azerstar.screen.Menus;
import com.azerstar.util.Choice;
import com.azerstar.util.DataTable;
import com.azerstar.util.Db;
import com.azerstar.util.FormDialog;
import com.azerstar.util.ListScreen;
import com.azerstar.util.Role;
import com.azerstar.util.Row;
import com.azerstar.util.Session;
import com.azerstar.util.Ui;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Adminlər (yalnız baş admin yarada və dəyişə bilər): baş adminlər və filial adminləri. */
public class AdminsScreen extends ListScreen {

    public AdminsScreen() {
        super("Adminlər", Menus::settings,
                DataTable.create("Ad", "firstname", "Soyad", "lastname", "İstifadəçi adı", "username",
                        "Rol", "role_text", "Filiallar", "branches"),
                false);
        init(Ui.primary("Admin əlavə et", () -> openForm(null)),
                Ui.button("Düzəliş et", this::edit),
                Ui.danger("Sil", this::delete));
    }

    @Override
    protected ObservableList<Row> load() {
        ObservableList<Row> admins = Db.query("SELECT u.* FROM user_account u WHERE u.role IN ('SUPER_ADMIN', 'BRANCH_ADMIN') ORDER BY u.role, u.account_id");
        Map<Integer, List<String>> branches = new HashMap<>();
        for (Row b : Db.query("SELECT ub.account_id, br.name FROM user_branch ub JOIN branch br ON br.branch_id = ub.branch_id ORDER BY br.branch_id")) {
            branches.computeIfAbsent(b.integer("account_id"), k -> new ArrayList<>()).add(b.str("name"));
        }
        for (Row a : admins) {
            Role role = Role.of(a.str("role"));
            a.put("role_text", role.label());
            a.put("branches", role == Role.SUPER_ADMIN ? "Hamısı" : String.join(", ", branches.getOrDefault(a.integer("account_id"), List.of())));
        }
        return admins;
    }

    private void edit() {
        Row r = selected();
        if (r != null) openForm(r);
    }

    private void delete() {
        Row r = selected();
        if (r == null) return;
        int id = r.integer("account_id");
        if (id == Session.getAccountId()) {
            Ui.warn("Özünüzü silə bilməzsiniz.");
            return;
        }
        if (Role.of(r.str("role")) == Role.SUPER_ADMIN && superAdminsExcept(id) == 0) {
            Ui.warn("Son baş adminı silmək olmaz.");
            return;
        }
        if (!Ui.confirm(r.str("username") + " adminı silinsin?")) return;
        if (Ui.run(() -> Db.update("DELETE FROM user_account WHERE account_id = ?", id))) reload();
    }

    private static int superAdminsExcept(int id) {
        return Db.queryOne("SELECT COUNT(*) AS n FROM user_account WHERE role = 'SUPER_ADMIN' AND account_id <> ?", id).integer("n");
    }

    private void openForm(Row existing) {
        FormDialog f = new FormDialog(existing == null ? "Yeni admin" : "Admina düzəliş");
        TextField first = f.text("Ad");
        TextField last = f.text("Soyad");
        TextField username = f.text("İstifadəçi adı");
        PasswordField password = f.password(existing == null ? "Şifrə" : "Yeni şifrə (boş = dəyişmir)");
        ComboBox<Choice> role = f.combo("Rol", List.of(new Choice(1, Role.BRANCH_ADMIN.label()), new Choice(2, Role.SUPER_ADMIN.label())));

        Map<Integer, CheckBox> branchBoxes = new LinkedHashMap<>();
        for (Choice b : Session.getAllowedBranches()) {
            branchBoxes.put(b.id(), f.check("Filial: " + b.label(), false));
        }
        f.addInfo("Baş admin bütün filialları, ayarları və verilənlər bazası bağlantısını idarə edir. "
                + "Filial admini yalnız seçilən filialların istifadəçilərini və çənlərini idarə edir.");

        // baş adminə filial seçmək lazım deyil: hamısını görür
        role.setOnAction(e -> {
            boolean superAdmin = role.getValue().id() == 2;
            for (CheckBox b : branchBoxes.values()) {
                b.setDisable(superAdmin);
                if (superAdmin) b.setSelected(true);
            }
        });

        if (existing == null) {
            role.setValue(role.getItems().get(0));
            CheckBox current = branchBoxes.get(Session.getBranchId());
            if (current != null) current.setSelected(true);
        } else {
            first.setText(existing.str("firstname"));
            last.setText(existing.str("lastname"));
            username.setText(existing.str("username"));
            role.setValue(role.getItems().get(Role.of(existing.str("role")) == Role.SUPER_ADMIN ? 1 : 0));
            for (Row b : Db.query("SELECT branch_id FROM user_branch WHERE account_id = ?", existing.integer("account_id"))) {
                CheckBox box = branchBoxes.get(b.integer("branch_id"));
                if (box != null) box.setSelected(true);
            }
        }
        role.getOnAction().handle(null);

        boolean saved = f.show("Saxla", () -> {
            if (first.getText().isBlank() || last.getText().isBlank()) return "Ad və soyadı daxil edin.";
            if (username.getText().isBlank()) return "İstifadəçi adını daxil edin.";
            if (existing == null && password.getText().isBlank()) return "Şifrəni daxil edin.";
            if (role.getValue() == null) return "Rolu seçin.";
            boolean superAdmin = role.getValue().id() == 2;
            if (!superAdmin && branchBoxes.values().stream().noneMatch(CheckBox::isSelected)) return "Filial adminı üçün ən azı bir filial seçin.";
            if (existing != null && !superAdmin && Role.of(existing.str("role")) == Role.SUPER_ADMIN
                    && superAdminsExcept(existing.integer("account_id")) == 0) {
                return "Son baş adminin rolunu dəyişmək olmaz.";
            }
            int home = 1;
            for (Map.Entry<Integer, CheckBox> e : branchBoxes.entrySet()) {
                if (e.getValue().isSelected()) {
                    home = e.getKey();
                    break;
                }
            }
            final int homeBranch = home;
            final String roleValue = superAdmin ? Role.SUPER_ADMIN.name() : Role.BRANCH_ADMIN.name();
            Db.tx(tx -> {
                long id;
                if (existing == null) {
                    id = tx.insert("INSERT INTO user_account (firstname, lastname, username, password, branch_id, role) VALUES (?, ?, ?, ?, ?, ?)",
                            first.getText().trim(), last.getText().trim(), username.getText().trim(), password.getText(), homeBranch, roleValue);
                } else {
                    id = existing.integer("account_id");
                    tx.update("UPDATE user_account SET firstname = ?, lastname = ?, username = ?, branch_id = ?, role = ? WHERE account_id = ?",
                            first.getText().trim(), last.getText().trim(), username.getText().trim(), homeBranch, roleValue, id);
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
                return null;
            });
            return null;
        });
        if (saved) reload();
    }
}
