package com.azerstar.controller.goodsReceipt;

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

public class UpdateRetailGoodsReceptionController {


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
    private Label updateGoodsSuccessfullyMessage;

    private String currentUsername = Session.getCurrentUsername();
    private int retail_receipt_id;

    @FXML
    public void initialize() {
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
            Parent retailGoodsReceptionScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/goodsReceipt/retailGoodsReception.fxml"));
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.setScene(new Scene(retailGoodsReceptionScene));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRetailGoodsReception(int retail_receipt_id,String customer_name,double weight,double unit_price,double total_amount,String note){
        customerNameTextField.setText(customer_name);
        weightTextField.setText(String.valueOf(weight));
        unitPriceTextField.setText(String.valueOf(unit_price));
        totalAmountTextField.setText(String.valueOf(total_amount));
        noteTextField.setText(note);
        this.retail_receipt_id=retail_receipt_id;
    }
    @FXML
    public void confirmButtonOnAction(ActionEvent event) {
        String customer_name = customerNameTextField.getText().trim();
        double weight = Double.parseDouble(weightTextField.getText().trim());
        double unit_price = Double.parseDouble(unitPriceTextField.getText().trim());
        double total_amount = Double.parseDouble(totalAmountTextField.getText().trim());
        String note = noteTextField.getText().trim();
        if(customer_name.isEmpty()){
            notnullFailedMessage.setText("Müştəri adını daxil edin!");
            return;
        }
        if (weightTextField.getText() == null || weightTextField.getText().trim().isEmpty()) {
            notnullFailedMessage.setText("Çəki sahəsi boş ola bilməz!");
            return;
        }
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String updateQuery = "UPDATE retail_receipt SET customer_name = ?, weight = ?, unit_price = ?, total_amount = ?, note = ? WHERE retail_receipt_id = ?";
        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(updateQuery);
            preparedStatement.setString(1, customer_name);
            preparedStatement.setDouble(2, weight);
            preparedStatement.setDouble(3, unit_price);
            preparedStatement.setDouble(4, total_amount);
            preparedStatement.setString(5, note);
            preparedStatement.setString(6, String.valueOf(retail_receipt_id));
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                updateGoodsSuccessfullyMessage.setText("Məlumatlar uğurla yeniləndi.");
                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(e -> SceneNavigator.goToScene(event, confirmButton, "/com/azerstar/view/goodsReceipt/retailGoodsReception.fxml"));
                pause.play();
            } else {
                notnullFailedMessage.setText("İstifadəçi tapılmadı.");
            }

        }catch (Exception e){
            e.printStackTrace();
            notnullFailedMessage.setText("Sehv bas verdi");
        }
    }



}
