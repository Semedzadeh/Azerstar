package com.azerstar.Controller;

import com.azerstar.config.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

public class CreateUserController {

    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Label createUserSuccessfullyMessage;
    @FXML
    private Label passwordFailedMessage;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField againPasswordField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField lastnameTextField;
    @FXML
    private TextField usernametextField;



    public void cancelButtonOnAction(ActionEvent event) {
        try {
            // Geri qayıdacağım FXML faylı
            Parent createAndEditUserScene = FXMLLoader.load(getClass().getResource("/Fxml/createAndEditUser.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) cancelButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(createAndEditUserScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();
        }
    }
    public void confirmButtonOnAction(ActionEvent event){

        if (passwordField.getText().equals(againPasswordField.getText())){
            createUser();
        }else {
            passwordFailedMessage.setText("Daxil etdiyiniz şifrələr eyni deyil");
        }
    }
    public void createUser(){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String firstname = nameTextField.getText();
        String lastname = lastnameTextField.getText();
        String username = usernametextField.getText();
        String password = passwordField.getText();

        String insertFields = "INSERT INTO user_account (firstname,lastname,username,password) VALUES ('";
        String insertValues = firstname + " ',' "+ lastname + " ',' "+ username + " ',' "+ password + "')";
        String insertToRegister = insertFields +insertValues;

        try{
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(insertToRegister);
            createUserSuccessfullyMessage.setText("Yeni Istifadəçi uğurla əlavə edildi");


        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }

    }








}
