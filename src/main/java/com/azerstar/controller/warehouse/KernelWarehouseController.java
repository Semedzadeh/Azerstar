package com.azerstar.controller.warehouse;

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

public class KernelWarehouseController {

    @FXML
    private Button backButton;
    @FXML
    private Button readyHazelnutKernelButton;
    @FXML
    private Button transferredHazelnutKernelButton;
    @FXML
    private ImageView profileAvatarImageView;

    private String currentUsername = Session.getCurrentUsername();
    @FXML
    public void profileAvatarImageMouseClicked() {
        Stage stage = (Stage) profileAvatarImageView.getScene().getWindow();
        ProfileMenu.attachProfileMenu(profileAvatarImageView, currentUsername, stage);
    }

    @FXML
    private void backButtonOnAction(ActionEvent event) {
        SceneNavigator.goToScene(event, backButton, "/com/azerstar/view/mainMenu.fxml");
    }
    public void readyHazelnutKernelButtonOnAction (ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent readyHazelnutKernelSecene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/warehouse/readyHazelnutKernel.fxml"));

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
            Parent transferredHazelnutKernelSecene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/warehouse/transferredHazelnutKernel.fxml"));

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
