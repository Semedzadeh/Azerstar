package com.azerstar.controller;

import com.azerstar.util.Nav;
import javafx.event.ActionEvent;

public class EntranceController {

    public void entranceButtonOnAction(ActionEvent event) {
        Nav.goFxml("/com/azerstar/view/login.fxml");
    }
}
