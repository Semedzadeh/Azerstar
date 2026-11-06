package com.azerstar.screen;

import com.azerstar.util.Cards;
import com.azerstar.util.Screen;
import com.azerstar.util.Ui;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/** Bölmənin alt menyusu: ikonlu böyük kartlar. */
public class MenuScreen extends Screen {

    /** icon və tone: bax util/Icons və style.css (.tone-*). */
    public record Tile(String text, String subtitle, String icon, String tone, Runnable action) {
    }

    public MenuScreen(String title, String subtitle, Runnable onBack, Tile... tiles) {
        super(title, onBack, false);

        FlowPane pane = new FlowPane(22, 22);
        pane.setAlignment(Pos.TOP_LEFT);
        pane.setPrefWrapLength(900);
        for (Tile tile : tiles) {
            pane.getChildren().add(Cards.menuCard(tile.icon(), tile.tone(), tile.text(), tile.subtitle(), true, tile.action()));
        }

        VBox box = new VBox(22, Ui.hint(subtitle), pane);
        box.setPadding(new Insets(14, 18, 0, 18));
        VBox.setMargin(pane, new Insets(10, 0, 0, 0));
        setContent(box);
    }
}
