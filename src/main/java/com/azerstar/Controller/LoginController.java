package com.azerstar.Controller;

import com.azerstar.config.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class LoginController {

    @FXML
    private Button backButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Button loginButton;




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
    public void validateLogin() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT * FROM user_account WHERE username = ? AND password = ?";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(verifyLogin);
            preparedStatement.setString(1, usernameTextField.getText());
            preparedStatement.setString(2, passwordTextField.getText());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                mainMenuForm(); // Giriş uğurlu
            } else {
                loginMessageLabel.setText("Daxil etdiyiniz istifadəçi adı və ya şifrə yanlışdır.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void mainMenuForm(){

        try{

            Parent mainMenuScene = FXMLLoader.load(getClass().getResource("/Fxml/mainMenu.fxml"));

            // Hal-hazırkı stage-i tap
            Stage mainMenuStage = (Stage) backButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            mainMenuStage.setScene(new Scene(mainMenuScene));
            mainMenuStage.show();

        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }


    }





}
