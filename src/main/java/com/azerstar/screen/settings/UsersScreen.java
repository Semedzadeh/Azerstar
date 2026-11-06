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
import javafx.scene.control.ComboBox;
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

/** İstifadəçilər: rol, görə biləcəyi filiallar və bölmə icazələri. */
public class UsersScreen extends ListScreen {

    public UsersScreen() {
        super("İstifadəçilər və icazələr", Menus::settings,
                DataTable.create("Ad", "firstname", "Soyad", "lastname", "İstifadəçi adı", "username",
                        "Rol", "role_text", "Filiallar", "branches", "Bölmələr", "perms"));
        init(Ui.primary("Əlavə et", () -> openForm(null)),
                Ui.button("Düzəliş et", this::edit),
                Ui.danger("Sil", this::delete));
    }

    @Override
    protected ObservableList<Row> load() {
        ObservableList<Row> users = Db.query("SELECT u.*, CASE u.role WHEN 'ADMIN' THEN 'Admin' ELSE 'İstifadəçi' END AS role_text "
                + "FROM user_account u ORDER BY u.account_id");

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
            boolean admin = "ADMIN".equals(u.str("role"));
            int id = u.integer("account_id");
            u.put("perms", admin ? "Hamısı" : String.join(", ", perms.getOrDefault(id, List.of())));
            u.put("branches", admin ? "Hamısı" : String.join(", ", branches.getOrDefault(id, List.of())));
        }
        return users;
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
        if ("ADMIN".equals(r.str("role")) && adminCountExcept(id) == 0) {
            Ui.warn("Son adminı silmək olmaz.");
            return;
        }
        if (!Ui.confirm(r.str("username") + " istifadəçisi silinsin?")) return;
        if (Ui.run(() -> Db.update("DELETE FROM user_account WHERE account_id = ?", id))) reload();
    }

    private static int adminCountExcept(int id) {
        return Db.queryOne("SELECT COUNT(*) AS n FROM user_account WHERE role = 'ADMIN' AND account_id <> ?", id).integer("n");
    }

    private void openForm(Row existing) {
        FormDialog f = new FormDialog(existing == null ? "Yeni istifadəçi" : "İstifadəçiyə düzəliş");
        TextField first = f.text("Ad");
        TextField last = f.text("Soyad");
        TextField username = f.text("İstifadəçi adı");
        PasswordField password = f.password(existing == null ? "Şifrə" : "Yeni şifrə (boş = dəyişmir)");
        ComboBox<Choice> role = f.combo("Rol", List.of(new Choice(1, "İstifadəçi"), new Choice(2, "Admin")));

        // hansı filialların məlumatını görə bilər
        Map<Integer, CheckBox> branchBoxes = new LinkedHashMap<>();
        for (Row b : Db.query("SELECT branch_id, name FROM branch ORDER BY branch_id")) {
            branchBoxes.put(b.integer("branch_id"), f.check("Filial: " + b.str("name"), false));
        }
        // hansı bölmələrdə işləyə bilər
        Map<Section, CheckBox> boxes = new EnumMap<>(Section.class);
        for (Section s : Section.values()) {
            boxes.put(s, f.check("Bölmə: " + s.title(), false));
        }
        role.setOnAction(e -> {
            boolean admin = role.getValue().id() == 2;
            for (CheckBox b : branchBoxes.values()) {
                b.setDisable(admin);
                if (admin) b.setSelected(true);
            }
            for (CheckBox b : boxes.values()) {
                b.setDisable(admin);
                if (admin) b.setSelected(true);
            }
        });

        if (existing == null) {
            role.setValue(role.getItems().get(0));
            // yeni istifadəçi üçün hazırkı filial əvvəlcədən seçilir
            CheckBox current = branchBoxes.get(Session.getBranchId());
            if (current != null) current.setSelected(true);
        } else {
            first.setText(existing.str("firstname"));
            last.setText(existing.str("lastname"));
            username.setText(existing.str("username"));
            role.setValue(role.getItems().get("ADMIN".equals(existing.str("role")) ? 1 : 0));
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
        role.getOnAction().handle(null);

        boolean saved = f.show("Saxla", () -> {
            if (first.getText().isBlank() || last.getText().isBlank()) return "Ad və soyadı daxil edin.";
            if (username.getText().isBlank()) return "İstifadəçi adını daxil edin.";
            if (existing == null && password.getText().isBlank()) return "Şifrəni daxil edin.";
            if (role.getValue() == null) return "Rolu seçin.";
            boolean admin = role.getValue().id() == 2;
            if (!admin && branchBoxes.values().stream().noneMatch(CheckBox::isSelected)) return "Ən azı bir filial seçin.";
            if (!admin && boxes.values().stream().noneMatch(CheckBox::isSelected)) return "Ən azı bir bölmə seçin.";
            if (existing != null && !admin && "ADMIN".equals(existing.str("role"))
                    && adminCountExcept(existing.integer("account_id")) == 0) {
                return "Son adminin rolunu dəyişmək olmaz.";
            }
            // əsas filial: seçilənlərin ilkidir (admin üçün 1)
            int home = 1;
            for (Map.Entry<Integer, CheckBox> e : branchBoxes.entrySet()) {
                if (e.getValue().isSelected()) {
                    home = e.getKey();
                    break;
                }
            }
            final int homeBranch = home;
            Db.tx(tx -> {
                long id;
                String roleValue = admin ? "ADMIN" : "USER";
                if (existing == null) {
                    id = tx.insert("INSERT INTO user_account (firstname, lastname, username, password, branch_id, role) VALUES (?, ?, ?, ?, ?, ?)",
                            first.getText().trim(), last.getText().trim(), username.getText().trim(), password.getText(),
                            homeBranch, roleValue);
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
                if (!admin) {
                    for (Map.Entry<Section, CheckBox> e : boxes.entrySet()) {
                        if (e.getValue().isSelected()) {
                            tx.update("INSERT INTO user_permission (account_id, section) VALUES (?, ?)", id, e.getKey().name());
                        }
                    }
                }
                return null;
            });
            return null;
        });
        if (saved) reload();
    }
}
