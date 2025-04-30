package com.azerstar.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateAndEditUserController {

    @FXML
    private Button backButton;
    @FXML
    private Button createUserButton;

    public void backButtonOnAction(ActionEvent event) {
        try {
            // Geri qayıdacağım FXML faylı
            Parent settingsScene = FXMLLoader.load(getClass().getResource("/Fxml/settings.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(settingsScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();
        }
    }
    public void createUserButtonOnAction(ActionEvent event) {
        try {
            // Logine getmek ucun FXML faylı
            Parent createUserScene = FXMLLoader.load(getClass().getResource("/Fxml/createUser.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) createUserButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(createUserScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
