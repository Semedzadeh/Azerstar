package com.azerstar.Controller;

import com.azerstar.Model.User;
import com.azerstar.Model.UserDAO;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateAndEditUserController implements Initializable {

    @FXML private Button backButton;
    @FXML private Button createUserButton;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> surnameColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, Integer> branchColumn; // branch_id int-dir!
    @FXML private TableView<User> usersTable;

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



}
