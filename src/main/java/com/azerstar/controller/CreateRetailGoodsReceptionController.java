package com.azerstar.controller;

import com.azerstar.config.DatabaseConnection;
import com.azerstar.util.NumberInputFilter;
import com.azerstar.util.ProfileMenu;
import com.azerstar.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CreateRetailGoodsReceptionController {

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
            Parent retailGoodsReceptionScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/retailGoodsReception.fxml"));
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.setScene(new Scene(retailGoodsReceptionScene));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void confirmButtonOnAction() {
        if (customerNameTextField.getText() != null && !customerNameTextField.getText().trim().isEmpty()
                && weightTextField.getText() != null && !weightTextField.getText().trim().isEmpty()) {
            createGoods();
        } else {
            notnullFailedMessage.setText("Müştəri adı və ya Məhsul miqdarı boş qoyula bilməz!");
        }
    }

    public void createGoods() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String customerName = customerNameTextField.getText();
        String note = noteTextField.getText();

        // Say tipində olan sahələri təmizləyib çevir
        double weight = NumberInputFilter.parseToDouble(weightTextField.getText());
        double unitPrice = NumberInputFilter.parseToDouble(unitPriceTextField.getText());
        double totalAmount = NumberInputFilter.parseToDouble(totalAmountTextField.getText());

        String insertToRetailGoods = "INSERT INTO retail_receipt (customer_name, weight, unit_price, total_amount, note) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(insertToRetailGoods);
            preparedStatement.setString(1, customerName);
            preparedStatement.setDouble(2, weight);
            preparedStatement.setDouble(3, unitPrice);
            preparedStatement.setDouble(4, totalAmount);
            preparedStatement.setString(5, note);

            preparedStatement.executeUpdate();
            createGoodsSuccessfullyMessage.setText("Uğurla əlavə edildi");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
