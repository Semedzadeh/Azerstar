package com.azerstar.controller;

import com.azerstar.util.ProfileMenu;
import com.azerstar.util.SceneNavigator;
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

public class GoodsReceiptController  {



    @FXML
    private Button retailGoodsReceptionButton;
    @FXML
    private Button wholesaleGoodsReceptionButton;
    @FXML
    private Button backButton;
    @FXML
    private ImageView profileAvatarImageView;

    private String currentUsername = Session.getCurrentUsername();
    @FXML
    public void profileAvatarImageMouseClicked() {
        Stage stage = (Stage) profileAvatarImageView.getScene().getWindow();
        ProfileMenu.attachProfileMenu(profileAvatarImageView, currentUsername, stage);
    }

    public void retailGoodsReceptionButtonOnAction (ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent retailGoodsReceptionScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/retailGoodsReception.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) retailGoodsReceptionButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(retailGoodsReceptionScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }
    public void wholesaleGoodsReceptionButtonOnAction (ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent wholesaleGoodsReceptionScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/wholesaleGoodsReception.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) wholesaleGoodsReceptionButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(wholesaleGoodsReceptionScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();

        }
    }
    @FXML
    private void backButtonOnAction(ActionEvent event) {
        SceneNavigator.goToScene(event, backButton, "/com/azerstar/view/mainMenu.fxml");
    }
}
