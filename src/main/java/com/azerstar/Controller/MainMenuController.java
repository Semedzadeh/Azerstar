package com.azerstar.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @FXML
    private ImageView profileAvatarImageView;
    @FXML
    private Button goodsReceiptButton;


    public void initialize(URL url, ResourceBundle resourceBundle) {
        File profileAvatarFile = new File("Images/profileavatar.jpg");
        Image profileAvatarImage = new Image(profileAvatarFile.toURI().toString());
        profileAvatarImageView.setImage(profileAvatarImage);
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
}
