package com.azerstar.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * Bütün modul ekranlarının ortaq çərçivəsi: yuxarıda geri düyməsi, başlıq,
 * (bir neçə filiala icazəsi olanlar üçün) filial filtri və istifadəçi kartı.
 */
public abstract class Screen {

    protected final BorderPane root = new BorderPane();

    protected Screen(String title, Runnable onBack, boolean branchFilter) {
        root.getStyleClass().add("screen");

        Button back = Ui.button("←  Geri", onBack == null ? () -> { } : onBack);
        back.setVisible(onBack != null);
        back.setManaged(onBack != null);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("screen-title");

        HBox right = new HBox(12);
        right.setAlignment(Pos.CENTER_RIGHT);
        if (branchFilter && Session.hasManyBranches()) {
            Label caption = new Label("Filial");
            caption.getStyleClass().add("user-branch");
            right.getChildren().addAll(caption, branchBox());
        }
        right.getChildren().add(Ui.userChip());

        StackPane titleBox = new StackPane(titleLabel);
        HBox top = new HBox(12, back, Ui.spacer(), titleBox, Ui.spacer(), right);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(12, 20, 12, 20));
        top.getStyleClass().add("top-bar");
        root.setTop(top);
    }

    private ComboBox<Choice> branchBox() {
        ComboBox<Choice> box = new ComboBox<>();
        box.getItems().add(new Choice(0, "Hamısı"));
        box.getItems().addAll(Session.getAllowedBranches());
        for (Choice c : box.getItems()) {
            if (c.id() == Session.getViewBranchId()) box.setValue(c);
        }
        box.setOnAction(e -> {
            Session.setViewBranchId(box.getValue().id());
            reload();
        });
        return box;
    }

    protected void setContent(Node content) {
        BorderPane.setMargin(content, new Insets(18, 22, 20, 22));
        root.setCenter(content);
    }

    /** Filial filtrini dəyişəndə çağırılır. */
    protected void reload() {
    }

    public void show() {
        Nav.go(root);
    }
}
