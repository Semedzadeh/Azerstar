package com.azerstar.Controller;

import com.azerstar.Util.SceneNavigator;
import com.azerstar.config.DatabaseConnection;
import com.azerstar.Util.Session;
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

    public void loginButtonOnAction(ActionEvent event) {
        String username = usernameTextField.getText().trim();
        String password = passwordTextField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            loginMessageLabel.setText("İstifadəçi adı və şifrə daxil edin");
            return;
        }

        if (validateLogin(username, password)) {
            Session.setCurrentUsername(username); // istifadəçi adı yadda saxlanılır
            goToMainMenu();
        } else {
            loginMessageLabel.setText("Daxil etdiyiniz istifadəçi adı və ya şifrə yanlışdır.");
        }
    }

    @FXML
    private void backButtonOnAction(ActionEvent event) {
        SceneNavigator.goToScene(event, backButton, "/Fxml/branchSelection.fxml");
    }

    private boolean validateLogin(String username, String password) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT * FROM user_account WHERE username = ? AND password = ?";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(verifyLogin)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void goToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/mainMenu.fxml"));
            Parent mainMenuScene = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(mainMenuScene));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
