package com.azerstar.util;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class SceneNavigator {

    public static void goToScene(ActionEvent event, Button button, String fxmlPath) {
        Nav.goFxml(fxmlPath);
    }
}
