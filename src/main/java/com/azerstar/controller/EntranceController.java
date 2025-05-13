package com.azerstar.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class EntranceController {


    @FXML
    private Button entranceButton;

    public void entranceButtonOnAction(ActionEvent event) {
        try {
            Parent branchSelectionScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/branchSelection.fxml"));
            Stage stage = (Stage) entranceButton.getScene().getWindow();
            stage.setScene(new Scene(branchSelectionScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
