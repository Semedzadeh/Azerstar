package com.azerstar.Controller;

import com.azerstar.Util.ProfileMenu;
import com.azerstar.Util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

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
        ProfileMenu.attachProfileMenu(profileAvatarImageView, currentUsername);
    }

    public void goodsReceiptButtonOnAction(ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent goodsReceiptButtonScene = FXMLLoader.load(getClass().getResource("/Fxml/goodsReceipt.fxml"));

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
            Parent analysisButtonScene = FXMLLoader.load(getClass().getResource("/Fxml/analysis.fxml"));

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
            Parent dryingScene = FXMLLoader.load(getClass().getResource("/Fxml/drying.fxml"));

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
            Parent kernelWarehouseScene = FXMLLoader.load(getClass().getResource("/Fxml/kernelWarehouse.fxml"));

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
            Parent reportScene = FXMLLoader.load(getClass().getResource("/Fxml/report.fxml"));

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
            Parent settingsScene = FXMLLoader.load(getClass().getResource("/Fxml/settings.fxml"));

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
