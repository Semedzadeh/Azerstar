package com.azerstar.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class BranchSelectionController {
    @FXML
    private Button backButton;

    public void backButtonOnAction(ActionEvent event) {
        try {
            // Geri qayıdacağım FXML faylı
            Parent branchSelectionScene = FXMLLoader.load(getClass().getResource("/Fxml/entrance.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(branchSelectionScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button zaqatalaButton;

    public void zaqatalaButtonOnAction(ActionEvent event) {
        try {
            // Logine getmek ucun FXML faylı
            Parent loginSelectionScene = FXMLLoader.load(getClass().getResource("/Fxml/login.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) zaqatalaButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(loginSelectionScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button shekiButton;

    public void shekiButtonOnAction(ActionEvent event) {
        try {
            // Logine getmek ucun FXML faylı
            Parent loginSelectionScene = FXMLLoader.load(getClass().getResource("/Fxml/login.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) shekiButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(loginSelectionScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
