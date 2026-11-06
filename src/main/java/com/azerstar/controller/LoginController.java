package com.azerstar.controller;

import com.azerstar.screen.Menus;
import com.azerstar.util.Choice;
import com.azerstar.util.Db;
import com.azerstar.util.Nav;
import com.azerstar.util.Reminders;
import com.azerstar.util.Row;
import com.azerstar.util.Section;
import com.azerstar.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class LoginController {

    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;

    @FXML
    public void initialize() {
        passwordTextField.setOnAction(e -> loginButtonOnAction(null));
    }

    public void loginButtonOnAction(ActionEvent event) {
        String username = usernameTextField.getText().trim();
        String password = passwordTextField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            loginMessageLabel.setText("İstifadəçi adı və şifrə daxil edin");
            return;
        }

        try {
            Row user = Db.queryOne("SELECT account_id, username, role, branch_id FROM user_account WHERE username = ? AND password = ?",
                    username, password);
            if (user == null) {
                loginMessageLabel.setText("Daxil etdiyiniz istifadəçi adı və ya şifrə yanlışdır.");
                return;
            }
            boolean admin = "ADMIN".equals(user.str("role"));
            int accountId = user.integer("account_id");

            Set<Section> allowed = EnumSet.noneOf(Section.class);
            for (Row p : Db.query("SELECT section FROM user_permission WHERE account_id = ?", accountId)) {
                try {
                    allowed.add(Section.valueOf(p.str("section")));
                } catch (IllegalArgumentException ignored) {
                    // naməlum bölmə adı varsa keç
                }
            }
            if (!admin && allowed.isEmpty()) {
                loginMessageLabel.setText("Bu istifadəçiyə heç bir bölmə üçün icazə verilməyib.");
                return;
            }

            // admin bütün filialları görür, digərləri yalnız özünə verilənləri
            List<Choice> branches = new ArrayList<>();
            String sql = admin
                    ? "SELECT branch_id, name FROM branch ORDER BY branch_id"
                    : "SELECT b.branch_id, b.name FROM user_branch ub JOIN branch b ON b.branch_id = ub.branch_id WHERE ub.account_id = ? ORDER BY b.branch_id";
            for (Row b : admin ? Db.query(sql) : Db.query(sql, accountId)) {
                branches.add(new Choice(b.integer("branch_id"), b.str("name")));
            }
            if (branches.isEmpty()) {
                loginMessageLabel.setText("Bu istifadəçiyə heç bir filial üçün icazə verilməyib.");
                return;
            }

            Session.login(accountId, user.str("username"), admin, allowed, branches, user.integer("branch_id"));
        } catch (Db.DbException e) {
            loginMessageLabel.setText(e.getMessage());
            return;
        }

        // bir filialı varsa seçim ekranı lazım deyil
        if (Session.hasManyBranches()) {
            Nav.goFxml("/com/azerstar/view/branchSelection.fxml");
        } else {
            Menus.main();
            Reminders.start();
        }
    }

    @FXML
    private void backButtonOnAction(ActionEvent event) {
        Nav.goFxml("/com/azerstar/view/entrance.fxml");
    }
}
