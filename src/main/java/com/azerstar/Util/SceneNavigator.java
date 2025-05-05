package com.azerstar.Util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneNavigator {

    public static void goToScene(ActionEvent event, Button button, String fxmlPath) {
        try {
            Parent newScene = FXMLLoader.load(SceneNavigator.class.getResource(fxmlPath));
            Stage stage = (Stage) button.getScene().getWindow();
            stage.setScene(new Scene(newScene));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
