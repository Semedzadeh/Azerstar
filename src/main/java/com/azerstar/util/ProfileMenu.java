package com.azerstar.util;

import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

/** İstifadəçi kartına basanda açılan menyu: kim daxil olub, hansı filiallar, filialı dəyiş, çıxış. */
public class ProfileMenu {

    private static ContextMenu contextMenu;

    public static void attachProfileMenu(Node anchor, String currentUsername, Stage primaryStage) {
        anchor.setOnMouseClicked(event -> showMenu(anchor, currentUsername));
    }

    private static void showMenu(Node anchor, String currentUsername) {
        if (contextMenu != null && contextMenu.isShowing()) {
            contextMenu.hide();
            return;
        }

        // hər dəfə yenidən qurulur ki, başqa istifadəçi girəndə köhnə ad qalmasın
        contextMenu = new ContextMenu();
        contextMenu.getStyleClass().add("profile-menu");

        Label name = new Label(currentUsername);
        name.getStyleClass().add("profile-menu-name");
        Label details = new Label("Filial: " + Session.accessLabel() + (Session.isAdmin() ? "  ·  Admin" : ""));
        details.getStyleClass().add("profile-menu-detail");
        VBox info = new VBox(2, name, details);
        info.setPadding(new Insets(6, 12, 6, 12));
        CustomMenuItem header = new CustomMenuItem(info, false);
        contextMenu.getItems().addAll(header, new SeparatorMenuItem());

        if (Session.hasManyBranches()) {
            MenuItem switchItem = new MenuItem("Filialı dəyiş");
            switchItem.setOnAction(e -> Nav.goFxml("/com/azerstar/view/branchSelection.fxml"));
            contextMenu.getItems().add(switchItem);
        }

        MenuItem logoutItem = new MenuItem("Çıxış et");
        logoutItem.getStyleClass().add("logout-item");
        logoutItem.setOnAction(e -> handleLogout());
        contextMenu.getItems().add(logoutItem);

        contextMenu.show(anchor, Side.BOTTOM, 0, 8);
    }

    private static void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Çıxış");
        alert.setHeaderText("Sistemdən çıxmaq istəyirsiniz?");
        alert.initOwner(Nav.stage());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            logout();
        }
    }

    /** Sessiyanı bağlayıb giriş ekranına qayıdır. */
    public static void logout() {
        Reminders.stop();
        Session.clearSession();
        Nav.goFxml("/com/azerstar/view/login.fxml");
    }
}
