package com.azerstar.util;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.Optional;

/** Kiçik ortaq UI köməkçiləri. */
public final class Ui {

    private Ui() {
    }

    public static void error(String message) {
        alert(Alert.AlertType.ERROR, "Xəta", message);
    }

    public static void warn(String message) {
        alert(Alert.AlertType.WARNING, "Xəbərdarlıq", message);
    }

    public static void info(String message) {
        alert(Alert.AlertType.INFORMATION, "Məlumat", message);
    }

    private static void alert(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        if (Nav.stage() != null) a.initOwner(Nav.stage());
        a.showAndWait();
    }

    public static boolean confirm(String message) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        a.setTitle("Təsdiq");
        a.setHeaderText(null);
        if (Nav.stage() != null) a.initOwner(Nav.stage());
        Optional<ButtonType> r = a.showAndWait();
        return r.isPresent() && r.get() == ButtonType.YES;
    }

    /** Bazaya yazan/oxuyan əməliyyatları icra edir, xəta olarsa istifadəçiyə göstərir. */
    public static boolean run(Runnable action) {
        try {
            action.run();
            return true;
        } catch (Db.DbException e) {
            error(e.getMessage());
            return false;
        }
    }

    public static Button button(String text, Runnable action) {
        Button b = new Button(text);
        b.setOnAction((ActionEvent e) -> action.run());
        return b;
    }

    public static Button primary(String text, Runnable action) {
        Button b = button(text, action);
        b.getStyleClass().add("primary-button");
        return b;
    }

    public static Button danger(String text, Runnable action) {
        Button b = button(text, action);
        b.getStyleClass().add("danger-button");
        return b;
    }

    public static Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    public static Label title(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("page-title");
        return l;
    }

    public static Label hint(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("hint");
        l.setWrapText(true);
        return l;
    }

    /** Rəngli kiçik nişan. kind: green, gray, red. */
    public static Label pill(String text, String kind) {
        Label l = new Label(text);
        l.getStyleClass().addAll("pill", "pill-" + kind);
        return l;
    }

    /** Kiçik məlumat kartı: solğun başlıq və qalın dəyər. */
    public static VBox stat(String caption, javafx.scene.Node value, boolean alert) {
        Label c = new Label(caption);
        c.getStyleClass().add("caption");
        VBox box = new VBox(4, c, value);
        box.getStyleClass().add("stat-card");
        if (alert) box.getStyleClass().add("stat-alert");
        return box;
    }

    public static VBox stat(String caption, String value, boolean alert) {
        Label v = new Label(value);
        v.getStyleClass().add("stat-value");
        return stat(caption, v, alert);
    }

    /** Yuxarı paneldə: dairəvi şəkil, istifadəçi adı və filial. Üzərinə basanda profil menyusu açılır. */
    public static javafx.scene.Node userChip() {
        String username = Session.getCurrentUsername() == null ? "?" : Session.getCurrentUsername();
        Label avatar = new Label(username.substring(0, 1).toUpperCase());
        avatar.getStyleClass().add("avatar-initial");

        Label name = new Label(username);
        name.getStyleClass().add("user-name");
        Label branch = new Label(Session.accessLabel() + (Session.isAnyAdmin() ? "  ·  " + Session.getRole().label() : ""));
        branch.getStyleClass().add("user-branch");
        VBox text = new VBox(1, name, branch);
        text.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        HBox chip = new HBox(10, avatar, text, Icons.of("chevron", 14));
        chip.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        chip.getStyleClass().add("user-chip");
        ProfileMenu.attachProfileMenu(chip, Session.getCurrentUsername(), null);
        return chip;
    }

    /** Boş sahəni 0 kimi deyil, null kimi qaytarır; yazılıbsa ədəd qaytarır. */
    public static Double number(javafx.scene.control.TextField field) {
        String s = field.getText();
        if (s == null || s.isBlank()) return null;
        try {
            return Double.parseDouble(s.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
