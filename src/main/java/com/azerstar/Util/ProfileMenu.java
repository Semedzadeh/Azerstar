
package com.azerstar.Util;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Optional;

public class ProfileMenu {

    private static ContextMenu contextMenu;

    public static void attachProfileMenu(ImageView profileImageView, String currentUsername) {
        profileImageView.setOnMouseClicked(event -> showMenu(event, profileImageView, currentUsername));
    }

    private static void showMenu(MouseEvent event, ImageView profileImageView, String currentUsername) {
        if (contextMenu != null && contextMenu.isShowing()) {
            contextMenu.hide();
            return;
        }

        if (contextMenu == null) {
            contextMenu = new ContextMenu();

            MenuItem userItem = new MenuItem("👤 " + currentUsername);
            userItem.setDisable(true);

            MenuItem logoutItem = new MenuItem("🚪 Çıxış et");
            logoutItem.setOnAction(e -> handleLogout(profileImageView));

            contextMenu.getItems().addAll(userItem, new SeparatorMenuItem(), logoutItem);
        }

        contextMenu.show(profileImageView, event.getScreenX(), event.getScreenY());
    }

    private static void handleLogout(ImageView profileImageView) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Çıxış");
        alert.setHeaderText("Sistemdən çıxmaq istəyirsiniz?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Parent root = FXMLLoader.load(ProfileMenu.class.getResource("/Fxml/login.fxml"));
                Stage stage = (Stage) profileImageView.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
