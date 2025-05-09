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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RetailGoodsReceptionController {

    @FXML
    private Button backButton;
    @FXML
    private ImageView profileAvatarImageView;
    @FXML
    private Text dateText;
    @FXML
    private Button createRetailGoodsReceptionButton;

    @FXML
    public void initialize()  {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        dateText.setText("Tarix: " + LocalDate.now().format(formatter));
    }


    private String currentUsername = Session.getCurrentUsername();
    @FXML
    public void profileAvatarImageMouseClicked() {
        Stage stage = (Stage) profileAvatarImageView.getScene().getWindow();
        ProfileMenu.attachProfileMenu(profileAvatarImageView, currentUsername, stage);
    }

    @FXML
    private void backButtonOnAction(ActionEvent event) {
        SceneNavigator.goToScene(event, backButton, "/com/azerstar/view/goodsReceipt.fxml");
    }
    public void createRetailGoodsReceptionButtonOnAction(ActionEvent event) {
        try {
            Parent createRetailGoodsReceptionScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/createRetailGoodsReception.fxml"));
            Stage stage = (Stage) createRetailGoodsReceptionButton.getScene().getWindow();
            stage.setScene(new Scene(createRetailGoodsReceptionScene));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
