package com.azerstar.controller;

import com.azerstar.model.GoodsReception;
import com.azerstar.model.GoodsReceptionDAO;
import com.azerstar.util.ProfileMenu;
import com.azerstar.util.SceneNavigator;
import com.azerstar.util.Session;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import java.time.LocalDateTime;
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
    private TableColumn<GoodsReception, String> dateTimeColumn;
    @FXML
    private TableColumn<GoodsReception, String> noteColumn;
    @FXML
    private TableColumn<GoodsReception, Double> totalAmountColumn;
    @FXML
    private TableColumn<GoodsReception, Double> unitPriceColumn;
    @FXML
    private TableColumn<GoodsReception, Double> weightColumn;
    @FXML
    private TableColumn<GoodsReception, String> custumerColumn;
    @FXML
    private TableView<GoodsReception> retailGoodsTable;

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

        ObservableList<GoodsReception> retailGoods = GoodsReceptionDAO.getAllRetailGoods();
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


}
