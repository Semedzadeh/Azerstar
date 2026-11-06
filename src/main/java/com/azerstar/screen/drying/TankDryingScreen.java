package com.azerstar.screen.drying;

import com.azerstar.screen.Menus;
import com.azerstar.util.Cards;
import com.azerstar.util.Db;
import com.azerstar.util.Row;
import com.azerstar.util.Screen;
import com.azerstar.util.Session;
import com.azerstar.util.Ui;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/** Çənlərin siyahısı: hər çən bir kart, basanda başladılır və ya açılır. */
public class TankDryingScreen extends Screen {

    private final FlowPane cards = new FlowPane(14, 14);

    public TankDryingScreen() {
        super("Çənlər ilə qurutma", Menus::drying, true);
        cards.setPadding(new Insets(6));
        ScrollPane scroll = new ScrollPane(cards);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("flat-scroll");
        setContent(scroll);
        reload();
    }

    @Override
    protected void reload() {
        cards.getChildren().clear();
        List<Row> tanks;
        try {
            tanks = Db.query("SELECT t.tank_id, t.name, t.capacity_kg, t.branch_id, b.name AS branch_name, "
                    + "d.drying_id, d.next_check_at, (d.next_check_at IS NOT NULL AND d.next_check_at <= NOW()) AS overdue, "
                    + "(SELECT COALESCE(SUM(s.weight_kg), 0) FROM drying_source s WHERE s.drying_id = d.drying_id) AS total_kg, "
                    + "(SELECT c.kernel_moisture FROM drying_check c WHERE c.drying_id = d.drying_id ORDER BY c.check_time DESC, c.check_id DESC LIMIT 1) AS last_moisture "
                    + "FROM tank t JOIN branch b ON b.branch_id = t.branch_id "
                    + "LEFT JOIN drying d ON d.tank_id = t.tank_id AND d.status = 'ACTIVE' "
                    + "WHERE t.active = 1" + Session.branchFilter("t.branch_id") + " ORDER BY t.branch_id, t.tank_id");
        } catch (Db.DbException e) {
            Ui.error(e.getMessage());
            return;
        }
        if (tanks.isEmpty()) {
            cards.getChildren().add(Ui.hint("Çən yoxdur. Ayarlar → Qurutma çənləri bölməsindən çən əlavə edin."));
            return;
        }
        for (Row t : tanks) cards.getChildren().add(card(t));
    }

    private Node card(Row t) {
        boolean busy = !t.isNull("drying_id");
        boolean overdue = t.integer("overdue") == 1;

        Label name = new Label(t.str("name"));
        name.getStyleClass().add("card-title");
        Label branch = new Label(t.str("branch_name"));
        branch.getStyleClass().add("card-subtitle");
        VBox titles = new VBox(1, name, branch);

        Label pill = overdue ? Ui.pill("Vaxtı çatıb", "red") : busy ? Ui.pill("Dolu", "green") : Ui.pill("Boş", "gray");
        HBox head = new HBox(10, Cards.badge("tank", busy ? "amber" : "gray", 34), titles, Ui.spacer(), pill);
        head.setAlignment(Pos.CENTER_LEFT);

        double capacity = t.number("capacity_kg");
        ProgressBar bar = new ProgressBar(!busy ? 0 : capacity > 0 ? Math.min(1, t.number("total_kg") / capacity) : 1);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setMinHeight(8);
        bar.setPrefHeight(8);
        if (busy && capacity <= 0) bar.getStyleClass().add("bar-gold");

        VBox box = new VBox(12, head, bar);
        if (busy) {
            Label metric = new Label(Row.format(t.number("total_kg")) + " kq");
            metric.getStyleClass().add("card-metric");
            Label moisture = new Label(t.isNull("last_moisture") ? "Nəmlik hələ ölçülməyib" : "Son nəmlik: " + t.text("last_moisture") + " %");
            moisture.getStyleClass().add("card-subtitle");
            Label next = new Label(t.isNull("next_check_at") ? "Yoxlama təyin edilməyib" : "Yoxlama: " + t.text("next_check_at"));
            next.getStyleClass().add(overdue ? "error-text" : "card-subtitle");
            box.getChildren().addAll(metric, moisture, next);
        } else {
            Label hint = new Label(capacity > 0 ? "Tutum: " + Row.format(capacity) + " kq" : "Tutum təyin edilməyib");
            hint.getStyleClass().add("card-subtitle");
            Label action = new Label("Başlatmaq üçün basın");
            action.getStyleClass().add("hint");
            box.getChildren().addAll(hint, action);
        }
        box.setPadding(new Insets(14));
        box.setPrefSize(220, 196);
        box.getStyleClass().add("card");
        box.getStyleClass().add(overdue ? "card-overdue" : busy ? "card-busy" : "card-free");
        box.setOnMouseClicked(e -> open(t, busy));
        return box;
    }

    private void open(Row t, boolean busy) {
        String name = t.str("name");
        if (busy) {
            new DryingDetailScreen(t.integer("drying_id"), name, () -> new TankDryingScreen().show()).show();
            return;
        }
        int id = DryingForms.start(t.integer("tank_id"), name, t.integer("branch_id"));
        if (id > 0) new DryingDetailScreen(id, name, () -> new TankDryingScreen().show()).show();
    }
}
