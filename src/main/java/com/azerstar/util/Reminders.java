package com.azerstar.util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Qurutmada nəmlik yoxlama vaxtı çatanda ekranda xəbərdarlıq göstərir (tətbiq açıq olanda). */
public final class Reminders {

    // eyni xəbərdarlığı bu müddətdən tez təkrar göstərmə
    private static final long REPEAT_MINUTES = 15;

    private static Timeline timeline;
    private static final Map<Integer, Instant> lastShown = new HashMap<>();

    private Reminders() {
    }

    public static void start() {
        stop();
        timeline = new Timeline(new KeyFrame(Duration.seconds(60), e -> check()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        check();
    }

    public static void stop() {
        if (timeline != null) timeline.stop();
        timeline = null;
        lastShown.clear();
    }

    public static void check() {
        if (!Session.can(Section.DRYING)) return;
        List<Row> due;
        try {
            due = Db.query("SELECT d.drying_id, d.drying_type, d.location, t.name AS tank_name, d.next_check_at "
                    + "FROM drying d LEFT JOIN tank t ON t.tank_id = d.tank_id "
                    + "WHERE d.status = 'ACTIVE' AND d.next_check_at IS NOT NULL AND d.next_check_at <= NOW()"
                    + Session.branchFilter("d.branch_id"));
        } catch (Db.DbException e) {
            return;
        }
        Instant now = Instant.now();
        for (Row r : due) {
            int id = r.integer("drying_id");
            Instant last = lastShown.get(id);
            if (last != null && last.plusSeconds(REPEAT_MINUTES * 60).isAfter(now)) continue;
            lastShown.put(id, now);

            String where = "TANK".equals(r.str("drying_type")) ? r.str("tank_name") : r.str("location");
            Alert a = new Alert(Alert.AlertType.INFORMATION,
                    where + " qurutmasını yoxlayın: fındıqdan nümunə götürüb ləpənin nəmliyini ölçməyin vaxtıdır.\n"
                            + "Planlaşdırılan vaxt: " + Row.format(r.get("next_check_at")),
                    ButtonType.OK);
            a.setTitle("Qurutma xatırlatması");
            a.setHeaderText("Nəmliyi yoxlayın");
            if (Nav.stage() != null) a.initOwner(Nav.stage());
            a.show();
        }
    }
}
