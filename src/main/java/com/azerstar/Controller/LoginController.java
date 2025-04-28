package com.azerstar.Controller;

import com.azerstar.config.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;


public class LoginController{

    @FXML
    private Button backButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;




    public void loginButtonOnAction (ActionEvent event){

        if(usernameTextField.getText().isBlank() == false && passwordTextField.getText().isBlank()== false){
            validateLogin();
        }
        else{
            loginMessageLabel.setText("İstifadəçi adı və şifrə daxil edin");
        }


    }

    public void backButtonOnAction(ActionEvent event) {
        try {
            // Geri qayıdacağım FXML faylı
            Parent branchSelectionScene = FXMLLoader.load(getClass().getResource("/Fxml/branchSelection.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(branchSelectionScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void validateLogin(){

        DatabaseConnection connectNow = new DatabaseConnection();
        Collection connectDB = connectNow.getConnection();




    }


}
