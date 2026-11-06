package com.azerstar;

import com.azerstar.util.Nav;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/azerstar/view/entrance.fxml"));
        primaryStage.setTitle("AzərStar");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Nav.init(primaryStage, root);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
