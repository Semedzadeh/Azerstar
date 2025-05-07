package com.azerstar.controller;

import com.azerstar.util.ProfileMenu;
import com.azerstar.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController  {

    @FXML
    private Button goodsReceiptButton;
    @FXML
    private Button analysisButton;
    @FXML
    private Button dryingButton;
    @FXML
    private Button kernelWarehouseButton;
    @FXML
    private Button reportButton;
    @FXML
    private Button settingsButton;

    @FXML
    private ImageView profileAvatarImageView;

    private String currentUsername = Session.getCurrentUsername();

    @FXML
    public void profileAvatarImageMouseClicked() {
        Stage stage = (Stage) profileAvatarImageView.getScene().getWindow();
        ProfileMenu.attachProfileMenu(profileAvatarImageView, currentUsername, stage);
    }

    public void goodsReceiptButtonOnAction(ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent goodsReceiptButtonScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/goodsReceipt.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) goodsReceiptButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(goodsReceiptButtonScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }
    public void analysisButtonOnAction(ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent analysisButtonScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/analysis.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) analysisButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(analysisButtonScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }
    public void dryingButtonOnAction(ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent dryingScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/drying.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) dryingButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(dryingScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }
    public void kernelWarehouseButtonOnAction(ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent kernelWarehouseScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/kernelWarehouse.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) kernelWarehouseButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(kernelWarehouseScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }
    public void reportButtonOnAction(ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent reportScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/report.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) reportButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(reportScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }
    public void settingsButtonOnAction(ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent settingsScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/settings.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) settingsButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(settingsScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }
}
