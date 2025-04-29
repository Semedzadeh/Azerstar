package com.azerstar.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ReportController {

    @FXML
    private Button backButton;
    @FXML
    private Button goodsReceptionReportButton;
    @FXML
    private Button finishedKernelReportButton;
    @FXML
    private Button transferredKernelReportButton;


    public void goodsReceptionReportButtonOnAction (ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent goodsReceptionReportScene = FXMLLoader.load(getClass().getResource("/Fxml/goodsReceptionReport.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) goodsReceptionReportButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(goodsReceptionReportScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }
    public void finishedKernelReportButtonOnAction (ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent finishedKernelReportScene = FXMLLoader.load(getClass().getResource("/Fxml/finishedKernelReport.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) finishedKernelReportButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(finishedKernelReportScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }
    public void transferredKernelReportButtonOnAction (ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent transferredKernelReportScene = FXMLLoader.load(getClass().getResource("/Fxml/transferredKernelReport.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) transferredKernelReportButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(transferredKernelReportScene));
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
            e.getCause();
        }
    }

}
