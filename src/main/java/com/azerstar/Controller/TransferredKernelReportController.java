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

public class TransferredKernelReportController {

    @FXML
    private Button backButton;
    @FXML
    private ImageView profileAvatarImageView;

    private String currentUsername = Session.getCurrentUsername();
    @FXML
    public void profileAvatarImageMouseClicked() {
        ProfileMenu.attachProfileMenu(profileAvatarImageView, currentUsername);
    }

    @FXML
    private void backButtonOnAction(ActionEvent event) {
        SceneNavigator.goToScene(event, backButton, "/Fxml/report.fxml");
    }

}
