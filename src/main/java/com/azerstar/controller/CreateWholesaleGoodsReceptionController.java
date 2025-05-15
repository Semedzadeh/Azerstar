package com.azerstar.controller;

import com.azerstar.config.DatabaseConnection;
import com.azerstar.util.NumberInputFilter;
import com.azerstar.util.ProfileMenu;
import com.azerstar.util.SceneNavigator;
import com.azerstar.util.Session;
import javafx.animation.PauseTransition;
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
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateWholesaleGoodsReceptionController {
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
    public void initialize() {
        // Tarixi göstər
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy / HH:mm");
        dateTimeText.setText("Tarix / saat: " + LocalDateTime.now().format(formatter));

        // Rəqəm məhdudiyyəti qoy
        NumberInputFilter.allowOnlyDoubles(weightTextField);
        NumberInputFilter.allowOnlyDoubles(unitPriceTextField);
        NumberInputFilter.allowOnlyDoubles(totalAmountTextField);
    }
    @FXML
    public void profileAvatarImageMouseClicked() {
        Stage stage = (Stage) profileAvatarImageView.getScene().getWindow();
        ProfileMenu.attachProfileMenu(profileAvatarImageView, currentUsername, stage);
    }
    @FXML
    public void cancelButtonOnAction(ActionEvent event) {
        try {
            Parent retailGoodsReceptionScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/wholesaleGoodsReception.fxml"));
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.setScene(new Scene(retailGoodsReceptionScene));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void confirmButtonOnAction(ActionEvent event) {
        if (customerNameTextField.getText() != null && !customerNameTextField.getText().trim().isEmpty()
                && weightTextField.getText() != null && !weightTextField.getText().trim().isEmpty()) {
            boolean success = createGoods();
            if (success) {
                createGoodsSuccessfullyMessage.setText("Uğurla əlavə edildi!");

                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(e -> SceneNavigator.goToScene(event, confirmButton, "/com/azerstar/view/wholesaleGoodsReception.fxml"));
                pause.play();
            } else {
                notnullFailedMessage.setText("Məlumatı əlavə etmək mümkün olmadı!");
            }

        } else {
            notnullFailedMessage.setText("Müştəri adı və ya Məhsul miqdarı boş qoyula bilməz!");
        }
    }
    private boolean createGoods() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String customerName = customerNameTextField.getText();
        String note = noteTextField.getText();

        double weight = NumberInputFilter.parseToDouble(weightTextField.getText());
        double unitPrice = NumberInputFilter.parseToDouble(unitPriceTextField.getText());
        double totalAmount = NumberInputFilter.parseToDouble(totalAmountTextField.getText());

        String insertToWholesaleGoods = "INSERT INTO wholesale_receipt (customer_name, weight, unit_price, total_amount, note) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(insertToWholesaleGoods);
            preparedStatement.setString(1, customerName);
            preparedStatement.setDouble(2, weight);
            preparedStatement.setDouble(3, unitPrice);
            preparedStatement.setDouble(4, totalAmount);
            preparedStatement.setString(5, note);

            preparedStatement.executeUpdate();
            createGoodsSuccessfullyMessage.setText("Uğurla əlavə edildi");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
