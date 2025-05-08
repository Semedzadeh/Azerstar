package com.azerstar.controller;

import com.azerstar.config.DatabaseConnection;
import com.azerstar.util.ProfileMenu;
import com.azerstar.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateRetailGoodsReceptionController {

    @FXML
    private Button cancelButton;
    @FXML
    private ImageView profileAvatarImageView;
    @FXML
    private Text dateTimeText;
    @FXML
    private Button confirmButton;
    @FXML
    private TextField customerNameTextField;
    @FXML
    private TextField noteTextField;
    @FXML
    private TextField totalAmountTextField;
    @FXML
    private TextField unitPriceTextField;
    @FXML
    private TextField weightTextField;
    @FXML
    private Label notnullFailedMessage;
    @FXML
    private Label createGoodsSuccessfullyMessage;


    private String currentUsername = Session.getCurrentUsername();

    @FXML
    public void profileAvatarImageMouseClicked() {
        Stage stage = (Stage) profileAvatarImageView.getScene().getWindow();
        ProfileMenu.attachProfileMenu(profileAvatarImageView, currentUsername, stage);
    }
    @FXML
    public void initialize() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy / HH:mm");
        dateTimeText.setText("Tarix / saat: " + LocalDateTime.now().format(formatter));
    }


    public void cancelButtonOnAction(ActionEvent event) {
        try {
            Parent retailGoodsReceptionScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/retailGoodsReception.fxml"));
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.setScene(new Scene(retailGoodsReceptionScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();
        }
    }
    public void confirmButtonOnAction(){
        if (customerNameTextField.getText() != null && !customerNameTextField.getText().trim().isEmpty()
                && weightTextField.getText() != null && !weightTextField.getText().trim().isEmpty()) {

            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();



        }else {
            notnullFailedMessage.setText("Müştəri adı və ya Məhsul miqdarı boş qoyula bilməz!");
        }
    }
    public void createGoods(){

    }



}
