package com.azerstar.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateUserController {

    @FXML
    private Button cancelButton;

    public void cancelButtonOnAction(ActionEvent event) {
        try {
            // Geri qayıdacağım FXML faylı
            Parent createAndEditUserScene = FXMLLoader.load(getClass().getResource("/Fxml/createAndEditUser.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) cancelButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(createAndEditUserScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();
        }
    }


}
