package com.azerstar.controller.report;

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
        Stage stage = (Stage) profileAvatarImageView.getScene().getWindow();
        ProfileMenu.attachProfileMenu(profileAvatarImageView, currentUsername, stage);
    }

    public void goodsReceptionReportButtonOnAction (ActionEvent event) {
        try {
            // Goods Receipt getmek ucun FXML faylı
            Parent goodsReceptionReportScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/report/goodsReceptionReport.fxml"));

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
            Parent finishedKernelReportScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/report/finishedKernelReport.fxml"));

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
            Parent transferredKernelReportScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/report/transferredKernelReport.fxml"));

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
        SceneNavigator.goToScene(event, backButton, "/com/azerstar/view/mainMenu.fxml");
    }

}
