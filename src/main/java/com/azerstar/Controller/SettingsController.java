package com.azerstar.Controller;

import com.azerstar.Util.ProfileMenu;
import com.azerstar.Util.SceneNavigator;
import com.azerstar.Util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class SettingsController {
    @FXML
    private Button backButton;
    @FXML
    private Button createEditDeleteUserButton;
    @FXML
    private Button userPermissionsButton;
    @FXML
    private Button databaseConnectionSettingsButton;
    @FXML
    private ImageView profileAvatarImageView;

    private String currentUsername = Session.getCurrentUsername();
    @FXML
    public void profileAvatarImageMouseClicked() {
        ProfileMenu.attachProfileMenu(profileAvatarImageView, currentUsername);
    }

    @FXML
    private void backButtonOnAction(ActionEvent event) {
        SceneNavigator.goToScene(event, backButton, "/Fxml/mainMenu.fxml");
    }

    public void createEditDeleteUserButtonOnAction (ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent createEditDeleteUserScene = FXMLLoader.load(getClass().getResource("/Fxml/createEditDeleteUser.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) createEditDeleteUserButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(createEditDeleteUserScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }
    public void userPermissionsButtonOnAction (ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent userPermissionsScene = FXMLLoader.load(getClass().getResource("/Fxml/userPermissions.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) userPermissionsButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(userPermissionsScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }
    public void databaseConnectionSettingsButtonOnAction (ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent databaseConnectionSettingsScene = FXMLLoader.load(getClass().getResource("/Fxml/databaseConnectionSettings.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) databaseConnectionSettingsButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(databaseConnectionSettingsScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }

}
