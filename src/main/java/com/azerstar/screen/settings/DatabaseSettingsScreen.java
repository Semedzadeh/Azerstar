package com.azerstar.screen.settings;

import com.azerstar.config.DatabaseConnection;
import com.azerstar.screen.Menus;
import com.azerstar.util.NumberInputFilter;
import com.azerstar.util.Screen;
import com.azerstar.util.Ui;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Properties;

/** Verilənlər bazası bağlantısı (yalnız baş admin). Ayarlar bu kompüterdəki azerstar.properties faylına yazılır. */
public class DatabaseSettingsScreen extends Screen {

    private final TextField host = new TextField();
    private final TextField port = new TextField();
    private final TextField database = new TextField();
    private final TextField user = new TextField();
    private final PasswordField password = new PasswordField();
    private final Label status = new Label();
    private final Button testButton = Ui.button("Bağlantını yoxla", this::test);
    private final Button saveButton = Ui.primary("Yoxla və saxla", this::save);

    public DatabaseSettingsScreen() {
        super("Verilənlər bazası bağlantısı", Menus::settings, false);

        Properties current = DatabaseConnection.readConfig();
        host.setText(current.getProperty("db.host"));
        port.setText(current.getProperty("db.port"));
        database.setText(current.getProperty("db.name"));
        user.setText(current.getProperty("db.user"));
        password.setPromptText("Dəyişmirsinizsə boş buraxın");
        NumberInputFilter.allowOnlyIntegers(port);
        for (TextField t : new TextField[]{host, port, database, user, password}) t.setPrefWidth(300);

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);
        addRow(grid, 0, "Server (host)", host);
        addRow(grid, 1, "Port", port);
        addRow(grid, 2, "Baza adı", database);
        addRow(grid, 3, "İstifadəçi", user);
        addRow(grid, 4, "Şifrə", password);

        status.setWrapText(true);
        status.setMaxWidth(460);
        HBox buttons = new HBox(10, testButton, saveButton);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Bağlantı ayarları");
        title.getStyleClass().add("login-title");
        VBox card = new VBox(16, title,
                Ui.hint("Bu ayarlar yalnız bu kompüterdə (azerstar.properties) saxlanılır. Yanlış ayar bütün istifadəçiləri bazadan ayırır, "
                        + "ona görə saxlamazdan əvvəl bağlantı yoxlanılır."),
                grid, status, buttons);
        card.getStyleClass().add("login-card");
        card.setPadding(new Insets(30));
        card.setMaxWidth(560);
        card.setMaxHeight(VBox.USE_PREF_SIZE);
        setContent(new StackPane(card));
    }

    private static void addRow(GridPane grid, int row, String label, javafx.scene.Node field) {
        Label l = new Label(label);
        l.getStyleClass().add("form-label");
        grid.add(l, 0, row);
        grid.add(field, 1, row);
    }

    /** Ekrandakı sahələrdən ayarlar; şifrə boşdursa köhnəsi saxlanılır. */
    private Properties entered() {
        Properties old = DatabaseConnection.readConfig();
        Properties p = new Properties();
        p.setProperty("db.host", host.getText().trim());
        p.setProperty("db.port", port.getText().trim());
        p.setProperty("db.name", database.getText().trim());
        p.setProperty("db.user", user.getText().trim());
        p.setProperty("db.password", password.getText().isEmpty() ? old.getProperty("db.password") : password.getText());
        return p;
    }

    private String check(Properties p) {
        if (p.getProperty("db.host").isEmpty() || p.getProperty("db.port").isEmpty()
                || p.getProperty("db.name").isEmpty() || p.getProperty("db.user").isEmpty()) {
            return "Server, port, baza adı və istifadəçi boş ola bilməz.";
        }
        return null;
    }

    private void test() {
        run(false);
    }

    private void save() {
        run(true);
    }

    /** Bağlantı yoxlanılır (arxa planda), uğurlu olarsa və lazımdırsa fayla yazılır. */
    private void run(boolean saveAfter) {
        Properties p = entered();
        String problem = check(p);
        if (problem != null) {
            show(problem, true);
            return;
        }
        testButton.setDisable(true);
        saveButton.setDisable(true);
        show("Bağlantı yoxlanılır…", false);
        Thread worker = new Thread(() -> {
            String error = DatabaseConnection.test(p);
            Platform.runLater(() -> {
                testButton.setDisable(false);
                saveButton.setDisable(false);
                if (error != null) {
                    show("Bağlanmaq olmadı: " + error, true);
                } else if (saveAfter) {
                    try {
                        DatabaseConnection.saveConfig(p);
                        password.clear();
                        show("Bağlantı uğurludur və ayarlar saxlanıldı.", false);
                    } catch (IOException e) {
                        show("Fayla yazmaq mümkün olmadı: " + e.getMessage(), true);
                    }
                } else {
                    show("Bağlantı uğurludur.", false);
                }
            });
        });
        worker.setDaemon(true);
        worker.start();
    }

    private void show(String text, boolean error) {
        status.setText(text);
        status.getStyleClass().removeAll("error-text", "hint");
        status.setStyle(error ? "" : "-fx-text-fill: #027A48; -fx-font-weight: bold;");
        status.getStyleClass().add(error ? "error-text" : "hint");
    }
}
