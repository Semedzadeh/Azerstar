package com.azerstar.controller;

import com.azerstar.model.RetailGoodsReception;
import com.azerstar.model.GoodsReceptionDAO;
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

public class RetailGoodsReceptionController  implements Initializable {

    @FXML
    private Button backButton;
    @FXML
    private ImageView profileAvatarImageView;
    @FXML
    private Text dateText;
    @FXML
    private Button createRetailGoodsReceptionButton;
    @FXML
    private TableColumn<RetailGoodsReception, String> dateTimeColumn;
    @FXML
    private TableColumn<RetailGoodsReception, String> noteColumn;
    @FXML
    private TableColumn<RetailGoodsReception, Double> totalAmountColumn;
    @FXML
    private TableColumn<RetailGoodsReception, Double> unitPriceColumn;
    @FXML
    private TableColumn<RetailGoodsReception, Double> weightColumn;
    @FXML
    private TableColumn<RetailGoodsReception, String> custumerColumn;
    @FXML
    private TableView<RetailGoodsReception> retailGoodsTable;
    @FXML
    private Button updateRetailGoodsReceptionButton;

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

        ObservableList<RetailGoodsReception> retailGoods = GoodsReceptionDAO.getAllRetailGoods();
        retailGoodsTable.setItems(retailGoods);
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
    public void createRetailGoodsReceptionButtonOnAction(ActionEvent event) {
        try {
            Parent createRetailGoodsReceptionScene = FXMLLoader.load(getClass().getResource("/com/azerstar/view/createRetailGoodsReception.fxml"));
            Stage stage = (Stage) createRetailGoodsReceptionButton.getScene().getWindow();
            stage.setScene(new Scene(createRetailGoodsReceptionScene));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void refreshRetailGoodsReceptionButtonOnAction(ActionEvent event) {
        ObservableList<RetailGoodsReception> refreshRetailGoods = GoodsReceptionDAO.getAllRetailGoods();
        retailGoodsTable.setItems(refreshRetailGoods);
        retailGoodsTable.refresh();
    }
    public void updateRetailGoodsReceptionButtonOnAction(ActionEvent event) {
        RetailGoodsReception selectRetailGoodsReception = retailGoodsTable.getSelectionModel().getSelectedItem();

        if (selectRetailGoodsReception != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/azerstar/view/updateRetailGoodsReception.fxml"));
                Parent updateRetailGoodsReception = loader.load();
                UpdateRetailGoodsReceptionController updateRetailGoodsReceptionController = loader.getController();
                updateRetailGoodsReceptionController.setRetailGoodsReception(
                        selectRetailGoodsReception.getRetail_receipt_id(),
                        selectRetailGoodsReception.getCustomer_name(),
                        selectRetailGoodsReception.getWeight(),
                        selectRetailGoodsReception.getUnit_price(),
                        selectRetailGoodsReception.getTotal_amount(),
                        selectRetailGoodsReception.getNote()
                );

                // Mövcud səhnəni götür və root-u dəyiş
                Scene currentScene = ((Node) event.getSource()).getScene();
                currentScene.setRoot(updateRetailGoodsReception);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("Xəbərdarlıq");
            warning.setHeaderText(null);
            warning.setContentText("Zəhmət olmasa düzəliş etmək üçün istifadəçi seçin!");
            warning.showAndWait();
        }




    }

}
