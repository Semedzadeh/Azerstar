package com.azerstar.controller;

import com.azerstar.screen.Menus;
import com.azerstar.util.Cards;
import com.azerstar.util.Section;
import com.azerstar.util.Session;
import com.azerstar.util.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

public class MainMenuController {

    @FXML
    private FlowPane grid;
    @FXML
    private HBox userBox;
    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        welcomeLabel.setText("Xoş gəlmisiniz, " + Session.getCurrentUsername());
        userBox.getChildren().add(Ui.userChip());

        // icazəsi olmayan bölmənin kartı solğun və bağlı olur
        grid.getChildren().addAll(
                Cards.menuCard("receipt", "hazel", "Mal qəbulu", "Pərakəndə və topdan qəbul", Session.can(Section.RECEIPT), Menus::receipt),
                Cards.menuCard("flask", "green", "Analiz", "Laboratoriya nəticələri", Session.can(Section.ANALYSIS), Menus::analysis),
                Cards.menuCard("drop", "amber", "Qurutma", "Çənlər və açıq qurutma", Session.can(Section.DRYING), Menus::drying),
                Cards.menuCard("warehouse", "slate", "Anbar", "Fındıq, qırma, ləpə, transfer", Session.can(Section.WAREHOUSE), Menus::warehouse),
                Cards.menuCard("bars", "plum", "Hesabat", "Hesabatlar və izlənmə", Session.can(Section.REPORT), Menus::report),
                Cards.menuCard("sliders", "gray", "Ayarlar", "İstifadəçilər, çənlər", Session.can(Section.SETTINGS), Menus::settings));
    }
}
