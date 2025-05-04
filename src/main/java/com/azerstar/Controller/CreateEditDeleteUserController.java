package com.azerstar.Controller;

import com.azerstar.Model.User;
import com.azerstar.Model.UserDAO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class CreateEditDeleteUserController implements Initializable {

    @FXML
    private Button backButton;
    @FXML
    private Button createUserButton;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> surnameColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, Integer> branchColumn; // branch_id int-dir!
    @FXML
    private TableView<User> usersTable;
    @FXML
    private Button editUserButton;
    @FXML
    private Button deleteUserButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        branchColumn.setCellValueFactory(new PropertyValueFactory<>("branch_id")); // int olaraq göstər

        ObservableList<User> users = UserDAO.getAllUsers();
        usersTable.setItems(users);
    }

    public void backButtonOnAction(ActionEvent event) {
        try {
            Parent settingsScene = FXMLLoader.load(getClass().getResource("/Fxml/settings.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(settingsScene));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createUserButtonOnAction(ActionEvent event) {
        try {
            Parent createUserScene = FXMLLoader.load(getClass().getResource("/Fxml/createUser.fxml"));
            Stage stage = (Stage) createUserButton.getScene().getWindow();
            stage.setScene(new Scene(createUserScene));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void editUserButtonOnAction(ActionEvent event) {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/updateUser.fxml"));
                Parent updateUserRoot = loader.load();

                UpdateUserController controller = loader.getController();
                controller.setUserData(
                        selectedUser.getFirstname(),
                        selectedUser.getLastname(),
                        selectedUser.getUsername(),
                        selectedUser.getPassword()
                );

                // Mövcud səhnəni götür və root-u dəyiş
                Scene currentScene = ((Node) event.getSource()).getScene();
                currentScene.setRoot(updateUserRoot);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("Xəbərdarlıq");
            warning.setHeaderText(null);
            warning.setContentText("Zəhmət olmasa düzəliş etmək üçün istifadəçi seçin!");
            warning.showAndWait();
        }
    }
    public void deleteUserButtonOnAction(ActionEvent event) {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Təsdiqlə");
            alert.setHeaderText("İstifadəçini silmək istədiyinizə əminsiniz?");
            alert.setContentText(selectedUser.getFirstname() + " " + selectedUser.getLastname() + " silinəcək.");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean isDeleted = UserDAO.deleteUser(selectedUser.getAccount_id());

                if (isDeleted) {
                    usersTable.getItems().remove(selectedUser);

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Uğurlu");
                    success.setHeaderText(null);
                    success.setContentText("İstifadəçi uğurla silindi.");
                    success.showAndWait();
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Xəta");
                    error.setHeaderText(null);
                    error.setContentText("İstifadəçi silinərkən xəta baş verdi.");
                    error.showAndWait();
                }
            }

        } else {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("Xəbərdarlıq");
            warning.setHeaderText(null);
            warning.setContentText("Zəhmət olmasa silmək üçün istifadəçi seçin!");
            warning.showAndWait();
        }
    }


}
