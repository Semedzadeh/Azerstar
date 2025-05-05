package com.azerstar.Controller;

import com.azerstar.Util.ProfileMenu;
import com.azerstar.Util.SceneNavigator;
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
        ProfileMenu.attachProfileMenu(profileAvatarImageView, currentUsername);
    }

    public void retailGoodsReceptionButtonOnAction (ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent retailGoodsReceptionScene = FXMLLoader.load(getClass().getResource("/Fxml/retailGoodsReception.fxml"));

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
            Parent wholesaleGoodsReceptionScene = FXMLLoader.load(getClass().getResource("/Fxml/wholesaleGoodsReception.fxml"));

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
        SceneNavigator.goToScene(event, backButton, "/Fxml/mainMenu.fxml");
    }
}
