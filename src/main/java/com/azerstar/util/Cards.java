package com.azerstar.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Menyu kartları və ikon nişanları. */
public final class Cards {

    private Cards() {
    }

    /** Rəngli yumru kvadrat içində ağ ikon. tone: hazel, green, amber, slate, plum, gray, gold. */
    public static Node badge(String icon, String tone, double size) {
        StackPane badge = new StackPane(Icons.of(icon, size * 0.5));
        badge.getStyleClass().addAll("icon-badge", "tone-" + tone);
        badge.setMinSize(size, size);
        badge.setPrefSize(size, size);
        badge.setMaxSize(size, size);
        return badge;
    }

    /** Böyük menyu kartı: solda ikon nişanı, sağda başlıq və izah. */
    public static Button menuCard(String icon, String tone, String title, String subtitle, boolean enabled, Runnable action) {
        Label t = new Label(title);
        t.getStyleClass().add("card-title");
        t.setWrapText(true);
        Label s = new Label(subtitle);
        s.getStyleClass().add("card-subtitle");
        s.setWrapText(true);
        VBox text = new VBox(4, t, s);
        text.setAlignment(Pos.CENTER_LEFT);

        HBox content = new HBox(16, badge(icon, tone, 56), text);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(0));
        content.setMaxWidth(240);

        Button button = new Button();
        button.setGraphic(content);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.getStyleClass().add("menu-card");
        button.setPrefSize(280, 112);
        button.setDisable(!enabled);
        button.setOnAction(e -> action.run());
        return button;
    }
}
