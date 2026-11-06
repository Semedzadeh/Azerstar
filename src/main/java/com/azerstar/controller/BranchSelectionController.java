package com.azerstar.controller;

import com.azerstar.screen.Menus;
import com.azerstar.util.Cards;
import com.azerstar.util.Choice;
import com.azerstar.util.ProfileMenu;
import com.azerstar.util.Reminders;
import com.azerstar.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

/** Girişdən sonra: yalnız istifadəçiyə icazə verilən filiallar göstərilir; baş admin üçün əlavə olaraq ayarlar kartı. */
public class BranchSelectionController {

    private static final String[] TONES = {"hazel", "green", "slate", "plum"};

    @FXML
    private FlowPane grid;

    @FXML
    public void initialize() {
        int i = 0;
        for (Choice branch : Session.getAllowedBranches()) {
            grid.getChildren().add(Cards.menuCard("building", TONES[i++ % TONES.length], branch.label(),
                    branch.label() + " filialının məlumatları", true, () -> choose(branch.id())));
        }
        // baş admin ayarları (adminlər, filiallar, baza bağlantısı) yalnız baş adminin seçim ekranında görünür
        if (Session.isSuperAdmin()) {
            grid.getChildren().add(Cards.menuCard("sliders", "gray", "Baş admin ayarları",
                    "Adminlər, filiallar, baza bağlantısı", true, Menus::settings));
        }
    }

    private void choose(int branchId) {
        Session.chooseBranch(branchId);
        Menus.main();
        Reminders.start();
    }

    /** Geri: giriş ekranına, sessiya bağlanır. */
    public void backButtonOnAction(ActionEvent event) {
        ProfileMenu.logout();
    }
}
