package com.azerstar.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {
    @FXML
    private Button backButton;
    @FXML
    private Button createAndEditUserButton;
    @FXML
    private Button userPermissionsButton;
    @FXML
    private Button databaseConnectionSettingsButton;

    public void backButtonOnAction(ActionEvent event) {
        try {
            // Geri qayıdacağım FXML faylı
            Parent mainMenuScene = FXMLLoader.load(getClass().getResource("/Fxml/mainMenu.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(mainMenuScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void createAndEditUserButtonOnAction (ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent createAndEditUserScene = FXMLLoader.load(getClass().getResource("/Fxml/createAndEditUser.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) createAndEditUserButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(createAndEditUserScene));
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
