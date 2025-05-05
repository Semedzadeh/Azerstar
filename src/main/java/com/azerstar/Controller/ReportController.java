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

public class ReportController {

    @FXML
    private Button backButton;
    @FXML
    private Button goodsReceptionReportButton;
    @FXML
    private Button finishedKernelReportButton;
    @FXML
    private Button transferredKernelReportButton;
    @FXML
    private ImageView profileAvatarImageView;

    private String currentUsername = Session.getCurrentUsername();
    @FXML
    public void profileAvatarImageMouseClicked() {
        ProfileMenu.attachProfileMenu(profileAvatarImageView, currentUsername);
    }

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


    @FXML
    private void backButtonOnAction(ActionEvent event) {
        SceneNavigator.goToScene(event, backButton, "/Fxml/mainMenu.fxml");
    }

}
