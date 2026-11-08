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

        // icazəsi olmayan bölmə heç göstərilmir
        addCard(Section.RECEIPT, "receipt", "hazel", "Mal qəbulu", "Pərakəndə və topdan qəbul", Menus::receipt);
        addCard(Section.ANALYSIS, "flask", "green", "Analiz", "Laboratoriya nəticələri", Menus::analysis);
        addCard(Section.DRYING, "drop", "amber", "Qurutma", "Çənlər və açıq qurutma", Menus::drying);
        addCard(Section.WAREHOUSE, "warehouse", "slate", "Anbar", "Fındıq, qırma, ləpə, transfer", Menus::warehouse);
        addCard(Section.REPORT, "bars", "plum", "Hesabat", "Hesabatlar və izlənmə", Menus::report);

        // filialın öz ayarları yalnız adminlərə göstərilir, sadə istifadəçi onu heç görmür
        if (Session.isAnyAdmin()) {
            grid.getChildren().add(Cards.menuCard("sliders", "teal", "Filial ayarları", "İstifadəçilər və çənlər", true, Menus::branches));
        }
    }

    private void addCard(Section section, String icon, String color, String title, String subtitle, Runnable action) {
        if (Session.can(section)) {
            grid.getChildren().add(Cards.menuCard(icon, color, title, subtitle, true, action));
        }
    }
}
