package com.azerstar.Controller;

import com.azerstar.Util.ProfileMenu;
import com.azerstar.Util.SceneNavigator;
import com.azerstar.Util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class DryingController {



    @FXML
    private Button tankDryingButton;
    @FXML
    private Button sunDryingButton;
    @FXML
    private Button backButton;

    @FXML
    private ImageView profileAvatarImageView;

    private String currentUsername = Session.getCurrentUsername();
    @FXML
    public void profileAvatarImageMouseClicked() {
        ProfileMenu.attachProfileMenu(profileAvatarImageView, currentUsername);
    }

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

    @FXML
    private void backButtonOnAction(ActionEvent event) {
        SceneNavigator.goToScene(event, backButton, "/Fxml/mainMenu.fxml");
    }


}
