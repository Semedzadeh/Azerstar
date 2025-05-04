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

public class UpdateUserController {


    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Label updateUserSuccessfullyMessage;
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

    private String originalUsername;

    public void setUserData(String firstname, String lastname, String username, String password) {
        nameTextField.setText(firstname);
        lastnameTextField.setText(lastname);
        usernametextField.setText(username);
        passwordField.setText(password);
        againPasswordField.setText(password);
        this.originalUsername = username;
    }

    public void confirmButtonOnAction(ActionEvent event) {
        String firstname = nameTextField.getText().trim();
        String lastname = lastnameTextField.getText().trim();
        String newUsername = usernametextField.getText().trim();
        String password = passwordField.getText().trim();
        String againPassword = againPasswordField.getText().trim();

        if (firstname.isEmpty() || lastname.isEmpty() || newUsername.isEmpty() || password.isEmpty() || againPassword.isEmpty()) {
            passwordUsernameFailedMessage.setText("Bütün sahələr doldurulmalıdır.");
            return;
        }

        if (!password.equals(againPassword)) {
            passwordUsernameFailedMessage.setText("Şifrələr eyni deyil.");
            return;
        }

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String updateQuery = "UPDATE user_account SET firstname = ?, lastname = ?, username = ?, password = ? WHERE username = ?";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(updateQuery);
            preparedStatement.setString(1, firstname);
            preparedStatement.setString(2, lastname);
            preparedStatement.setString(3, newUsername);
            preparedStatement.setString(4, password);
            preparedStatement.setString(5, originalUsername); // köhnə username ilə tapırıq

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                updateUserSuccessfullyMessage.setText("Məlumatlar uğurla yeniləndi.");
                passwordUsernameFailedMessage.setText("");
                originalUsername = newUsername; // gələcək dəyişikliklər üçün
            } else {
                passwordUsernameFailedMessage.setText("İstifadəçi tapılmadı.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            passwordUsernameFailedMessage.setText("Xəta baş verdi.");
        }
    }

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
}

