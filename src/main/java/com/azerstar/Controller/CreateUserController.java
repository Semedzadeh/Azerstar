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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CreateUserController {

    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Label createUserSuccessfullyMessage;
    @FXML
    private Label passwordUsernameFailedMessage;
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
            Parent createEditDeleteUserScene = FXMLLoader.load(getClass().getResource("/Fxml/createEditDeleteUser.fxml"));

            // Hal-hazırkı stage-i tap
            Stage stage = (Stage) cancelButton.getScene().getWindow();

            // Yeni scene təyin et və göstər
            stage.setScene(new Scene(createEditDeleteUserScene));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();
        }
    }
    public void confirmButtonOnAction(ActionEvent event){
        if (passwordField.getText().equals(againPasswordField.getText())) {

            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();

            String username = usernametextField.getText().trim(); // boşluqları silir

            try {
                // Username-in bazada olub olmadığını yoxla
                String checkUserQuery = "SELECT COUNT(*) FROM user_account WHERE username = ?";
                PreparedStatement checkStatement = connectDB.prepareStatement(checkUserQuery);
                checkStatement.setString(1, username);

                ResultSet resultSet = checkStatement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);

                if (count > 0) {
                    passwordUsernameFailedMessage.setText("Bu istifadəçi adı artıq mövcuddur");
                } else {
                    createUser(); // username unikal olduğuna görə yarat
                }

            } catch (Exception e) {
                e.printStackTrace();
                passwordUsernameFailedMessage.setText("Xəta baş verdi!");
            }

        } else {
            passwordUsernameFailedMessage.setText("Daxil etdiyiniz şifrələr eyni deyil");
        }

    }
    public void createUser() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String firstname = nameTextField.getText().trim();
        String lastname = lastnameTextField.getText().trim();
        String username = usernametextField.getText().trim();
        String password = passwordField.getText().trim();

        String insertToRegister = "INSERT INTO user_account (firstname, lastname, username, password) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(insertToRegister);
            preparedStatement.setString(1, firstname);
            preparedStatement.setString(2, lastname);
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, password);

            preparedStatement.executeUpdate();
            createUserSuccessfullyMessage.setText("Yeni istifadəçi uğurla əlavə edildi");

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }


}
