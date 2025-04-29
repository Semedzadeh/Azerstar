package com.azerstar.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class DryingController {



    @FXML
    private Button tankDryingButton;
    @FXML
    private Button sunDryingButton;
    @FXML
    private Button backButton;





    public void tankDryingButtonOnAction (ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent tankDryingScene = FXMLLoader.load(getClass().getResource("/Fxml/tankDrying.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) tankDryingButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(tankDryingScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }
    public void sunDryingButtonOnAction (ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent sunDryingButtonScene = FXMLLoader.load(getClass().getResource("/Fxml/sunDrying.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) sunDryingButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(sunDryingButtonScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }

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


}
