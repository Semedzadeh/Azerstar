package com.azerstar.util;

import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;

/** Pəncərədə ekranları dəyişir. Hər ekran eyni Scene-in kökünə qoyulur və pəncərə idarəsi ilə bükülür. */
public final class Nav {

    private static Stage stage;

    private Nav() {
    }

    public static void init(Stage primaryStage, Parent firstRoot) {
        stage = primaryStage;
        Scene scene = new Scene(WindowChrome.wrap(firstRoot), 1000, 600);
        scene.getStylesheets().add(Nav.class.getResource("/com/azerstar/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(560);
        stage.getIcons().add(appIcon());
    }

    public static Stage stage() {
        return stage;
    }

    public static void go(Parent root) {
        stage.getScene().setRoot(WindowChrome.wrap(root));
    }

    public static void goFxml(String fxmlPath) {
        try {
            go(FXMLLoader.load(Nav.class.getResource(fxmlPath)));
        } catch (IOException e) {
            e.printStackTrace();
            Ui.error("Ekranı açmaq mümkün olmadı: " + fxmlPath);
        }
    }

    /** Tapşırıq paneli üçün: qızılı dairədə "A". */
    private static WritableImage appIcon() {
        Canvas c = new Canvas(64, 64);
        GraphicsContext g = c.getGraphicsContext2D();
        g.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#FFC24D")), new Stop(1, Color.web("#F08C00"))));
        g.fillOval(2, 2, 60, 60);
        g.setFill(Color.web("#062A21"));
        g.setFont(Font.font("Segoe UI", FontWeight.BOLD, 40));
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.fillText("A", 32, 34);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return c.snapshot(params, null);
    }
}
