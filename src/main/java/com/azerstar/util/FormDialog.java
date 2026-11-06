package com.azerstar.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Supplier;

/** Əlavə et / düzəliş et pəncərələri üçün sadə forma. */
public class FormDialog {

    private final Stage stage = new Stage();
    private final GridPane grid = new GridPane();
    private final Label errorLabel = new Label();
    private int row = 0;
    private boolean saved = false;

    public FormDialog(String title) {
        stage.setTitle(title);
        stage.initModality(Modality.WINDOW_MODAL);
        if (Nav.stage() != null) stage.initOwner(Nav.stage());
        grid.setHgap(14);
        grid.setVgap(12);
        errorLabel.getStyleClass().add("error-text");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(420);
    }

    public Node addNode(String label, Node node) {
        Label l = new Label(label);
        l.getStyleClass().add("form-label");
        grid.add(l, 0, row);
        grid.add(node, 1, row);
        row++;
        return node;
    }

    public void addInfo(String text) {
        Label l = Ui.hint(text);
        l.setMaxWidth(420);
        grid.add(l, 0, row, 2, 1);
        row++;
    }

    public TextField text(String label) {
        TextField t = new TextField();
        t.setPrefWidth(260);
        addNode(label, t);
        return t;
    }

    public PasswordField password(String label) {
        PasswordField t = new PasswordField();
        t.setPrefWidth(260);
        addNode(label, t);
        return t;
    }

    public TextField number(String label) {
        TextField t = text(label);
        NumberInputFilter.allowOnlyDoubles(t);
        return t;
    }

    public <T> ComboBox<T> combo(String label, List<T> items) {
        ComboBox<T> c = new ComboBox<>();
        c.getItems().addAll(items);
        c.setPrefWidth(260);
        addNode(label, c);
        return c;
    }

    public ComboBox<String> editableCombo(String label, List<String> items) {
        ComboBox<String> c = combo(label, items);
        c.setEditable(true);
        return c;
    }

    public CheckBox check(String label, boolean selected) {
        CheckBox c = new CheckBox();
        c.setSelected(selected);
        addNode(label, c);
        return c;
    }

    public Stage stage() {
        return stage;
    }

    /** saver xəta mətni qaytarır; null qaytarsa pəncərə bağlanır. */
    public boolean show(String saveText, Supplier<String> saver) {
        Button save = Ui.primary(saveText, () -> {
            try {
                String err = saver.get();
                if (err == null) {
                    saved = true;
                    stage.close();
                } else {
                    errorLabel.setText(err);
                    stage.sizeToScene();
                }
            } catch (Db.DbException e) {
                errorLabel.setText(e.getMessage());
                stage.sizeToScene();
            }
        });
        Button cancel = Ui.button("Bağla", stage::close);
        HBox buttons = new HBox(10, Ui.spacer(), cancel, save);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Label heading = new Label(stage.getTitle());
        heading.getStyleClass().add("dialog-title");
        VBox box = new VBox(18, heading, grid, errorLabel, buttons);
        box.setPadding(new Insets(26, 28, 24, 28));
        box.setMinWidth(520);
        box.getStyleClass().add("dialog-box");
        Scene scene = new Scene(box);
        scene.getStylesheets().addAll(Nav.stage().getScene().getStylesheets());
        stage.setScene(scene);
        stage.showAndWait();
        return saved;
    }
}
