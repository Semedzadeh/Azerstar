package com.azerstar.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class FinishedKernelReportController {

    @FXML
    private Button backButton;

    public void backButtonOnAction(ActionEvent event) {
        try {
            // Geri qayıdacağım FXML faylı
            Parent mainMenuScene = FXMLLoader.load(getClass().getResource("/Fxml/report.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(mainMenuScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();
        }
    }

}
