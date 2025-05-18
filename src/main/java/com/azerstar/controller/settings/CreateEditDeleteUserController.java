package com.azerstar.controller.settings;

import com.azerstar.model.User;
import com.azerstar.model.UserDAO;
import com.azerstar.util.ProfileMenu;
import com.azerstar.util.SceneNavigator;
import com.azerstar.util.Session;
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
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
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
    private TableColumn<User, Integer> branchColumn;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private Button editUserButton;
    @FXML
    private Button deleteUserButton;

    @FXML
    private ImageView profileAvatarImageView;

    private String currentUsername = Session.getCurrentUsername();
    @FXML
    public void profileAvatarImageMouseClicked() {
        Stage stage = (Stage) profileAvatarImageView.getScene().getWindow();
        ProfileMenu.attachProfileMenu(profileAvatarImageView, currentUsername, stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        branchColumn.setCellValueFactory(new PropertyValueFactory<>("branch_id")); // int olaraq göstər

        ObservableList<User> users = UserDAO.getAllUsers();
        usersTable.setItems(users);
    }

    @FXML
    private void backButtonOnAction(ActionEvent event) {
        SceneNavigator.goToScene(event, backButton, "/com/azerstar/view/settings/settings.fxml");
    }

    public void createUserButtonOnAction(ActionEvent event) {
        try {
            Parent createUserScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/settings/createUser.fxml"));
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/azerstar/view/settings/updateUser.fxml"));
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
