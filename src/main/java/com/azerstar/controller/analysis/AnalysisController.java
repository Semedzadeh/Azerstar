package com.azerstar.controller.analysis;

import com.azerstar.util.ProfileMenu;
import com.azerstar.util.SceneNavigator;
import com.azerstar.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AnalysisController {

    @FXML
    private Button backButton;
    @FXML
    private ImageView profileAvatarImageView;
    @FXML
    private Text dateText;

    private String currentUsername = Session.getCurrentUsername();


    @FXML
    public void initialize()  {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        dateText.setText("Tarix: " + LocalDate.now().format(formatter));
    }
    @FXML
    public void profileAvatarImageMouseClicked() {
        Stage stage = (Stage) profileAvatarImageView.getScene().getWindow();
        ProfileMenu.attachProfileMenu(profileAvatarImageView, currentUsername, stage);
    }

    @FXML
    private void backButtonOnAction(ActionEvent event) {
        SceneNavigator.goToScene(event, backButton, "/com/azerstar/view/mainMenu.fxml");
    }



}
