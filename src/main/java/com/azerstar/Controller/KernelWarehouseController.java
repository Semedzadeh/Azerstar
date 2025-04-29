package com.azerstar.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class KernelWarehouseController {

    @FXML
    private Button backButton;
    @FXML
    private Button readyHazelnutKernelButton;
    @FXML
    private Button transferredHazelnutKernelButton;


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
            e.getCause();
        }
    }
    public void readyHazelnutKernelButtonOnAction (ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent readyHazelnutKernelSecene = FXMLLoader.load(getClass().getResource("/Fxml/readyHazelnutKernel.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) readyHazelnutKernelButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(readyHazelnutKernelSecene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }
    public void transferredHazelnutKernelButtonOnAction (ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent transferredHazelnutKernelSecene = FXMLLoader.load(getClass().getResource("/Fxml/transferredHazelnutKernel.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) transferredHazelnutKernelButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(transferredHazelnutKernelSecene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }

}
