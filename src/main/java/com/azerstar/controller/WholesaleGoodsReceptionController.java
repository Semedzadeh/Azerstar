package com.azerstar.controller;

import com.azerstar.model.RetailGoodsReception;
import com.azerstar.model.RetailGoodsReceptionDAO;
import com.azerstar.model.WholesaleGoodsReception;
import com.azerstar.model.WholesaleGoodsReceptionDAO;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class WholesaleGoodsReceptionController implements Initializable {

    @FXML
    private Button backButton;
    @FXML
    private ImageView profileAvatarImageView;
    @FXML
    private Text dateText;
    @FXML
    private Button createWholesaleGoodsReceptionButton;
    @FXML
    private Button refreshWholesaleGoodsReceptionButton;
    @FXML
    private Button updateWholesaleGoodsReceptionButton;
    @FXML
    private TableColumn<WholesaleGoodsReception, String> dateTimeColumn;
    @FXML
    private TableColumn<WholesaleGoodsReception, String> custumerColumn;
    @FXML
    private TableColumn<WholesaleGoodsReception, Double> weightColumn;
    @FXML
    private TableColumn<WholesaleGoodsReception, Double> unitPriceColumn;
    @FXML
    private TableColumn<WholesaleGoodsReception, Double> totalAmountColumn;
    @FXML
    private TableColumn<WholesaleGoodsReception, String> noteColumn;
    @FXML
    private TableView<WholesaleGoodsReception> wholesaleGoodsTable;


    private String currentUsername = Session.getCurrentUsername();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        dateText.setText("Tarix: " + LocalDate.now().format(formatter));

        custumerColumn.setCellValueFactory(new PropertyValueFactory<>("customer_name"));
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("date_time"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unit_price"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("total_amount"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));

        ObservableList<WholesaleGoodsReception> wholesaleGoods = WholesaleGoodsReceptionDAO.getAllWholesaleGoods();
        wholesaleGoodsTable.setItems(wholesaleGoods);
    }
    @FXML
    public void profileAvatarImageMouseClicked() {
        Stage stage = (Stage) profileAvatarImageView.getScene().getWindow();
        ProfileMenu.attachProfileMenu(profileAvatarImageView, currentUsername, stage);
    }


    @FXML
    private void backButtonOnAction(ActionEvent event) {
        SceneNavigator.goToScene(event, backButton, "/com/azerstar/view/goodsReceipt.fxml");
    }
    public void refreshWholesaleGoodsReceptionButtonOnAction(ActionEvent event) {
        ObservableList<WholesaleGoodsReception> refreshWholesaleGoods = WholesaleGoodsReceptionDAO.getAllWholesaleGoods();
        wholesaleGoodsTable.setItems(refreshWholesaleGoods);
        wholesaleGoodsTable.refresh();
    }
    public void createWholesaleGoodsReceptionButtonOnAction(ActionEvent event){
        try {
            Parent createWholesaleGoodsReceptionScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/createWholesaleGoodsReception.fxml"));
            Stage stage = (Stage) createWholesaleGoodsReceptionButton.getScene().getWindow();
            stage.setScene(new Scene(createWholesaleGoodsReceptionScene));
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void updateWholesaleGoodsReceptionButtonOnAction(ActionEvent event){
        WholesaleGoodsReception selectWholesaleGoodsReception = wholesaleGoodsTable.getSelectionModel().getSelectedItem();

        if (selectWholesaleGoodsReception != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/azerstar/view/updateWholesaleGoodsReception.fxml"));
                Parent updateWholesaleGoodsReception = loader.load();
                UpdateWholesaleGoodsReceptionController updateWholesaleGoodsReceptionController = loader.getController();
                updateWholesaleGoodsReceptionController.setWholesaleGoodsReception(
                        selectWholesaleGoodsReception.getWholesale_receipt_id(),
                        selectWholesaleGoodsReception.getCustomer_name(),
                        selectWholesaleGoodsReception.getWeight(),
                        selectWholesaleGoodsReception.getUnit_price(),
                        selectWholesaleGoodsReception.getTotal_amount(),
                        selectWholesaleGoodsReception.getNote()
                );

                // Mövcud səhnəni götür və root-u dəyiş
                Scene currentScene = ((Node) event.getSource()).getScene();
                currentScene.setRoot(updateWholesaleGoodsReception);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("Xəbərdarlıq");
            warning.setHeaderText(null);
            warning.setContentText("Zəhmət olmasa düzəliş etmək üçün seçim edin!");
            warning.showAndWait();
        }


    }


}
