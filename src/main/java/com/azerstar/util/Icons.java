package com.azerstar.util;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

import java.util.Map;

/** Sadə xətt ikonları (24x24 şəbəkədə), rəngi CSS-dən (.icon) gəlir. */
public final class Icons {

    private static final Map<String, String> PATHS = Map.ofEntries(
            Map.entry("receipt", "M6 3h12v18l-3-2-3 2-3-2-3 2V3z M9 8h6 M9 12h6 M9 16h3"),
            Map.entry("flask", "M9 3h6 M10 3v6.5L5.2 18a2 2 0 0 0 1.8 3h10a2 2 0 0 0 1.8-3L14 9.5V3 M7.5 15h9"),
            Map.entry("drop", "M12 3s6 6.2 6 11a6 6 0 0 1-12 0c0-4.8 6-11 6-11z"),
            Map.entry("warehouse", "M3 9l9-6 9 6v11a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V9z M9 21v-7h6v7"),
            Map.entry("bars", "M5 21V11 M12 21V4 M19 21v-8"),
            Map.entry("sliders", "M4 7h9 M17 7h3 M4 12h3 M11 12h9 M4 17h11 M19 17h1 M15 5v4 M9 10v4 M17 15v4"),
            Map.entry("tank", "M5 5.5C5 4.1 8.1 3 12 3s7 1.1 7 2.5v13c0 1.4-3.1 2.5-7 2.5s-7-1.1-7-2.5v-13z M5 5.5C5 6.9 8.1 8 12 8s7-1.1 7-2.5 M5 12c0 1.4 3.1 2.5 7 2.5s7-1.1 7-2.5"),
            Map.entry("sun", "M8 12a4 4 0 1 0 8 0a4 4 0 1 0-8 0z M12 2v2 M12 20v2 M2 12h2 M20 12h2 M4.9 4.9l1.4 1.4 M17.7 17.7l1.4 1.4 M4.9 19.1l1.4-1.4 M17.7 6.3l1.4-1.4"),
            Map.entry("layers", "M12 3l9 5-9 5-9-5 9-5z M3 12.5l9 5 9-5 M3 16.5l9 5 9-5"),
            Map.entry("package", "M21 8l-9-5-9 5v8l9 5 9-5V8z M3 8l9 5 9-5 M12 13v8"),
            Map.entry("kernel", "M12 3c4 3 6 6 6 10a6 6 0 0 1-12 0c0-4 2-7 6-10z M12 8v11"),
            Map.entry("transfer", "M4 8h15 M15 4l4 4-4 4 M20 16H5 M9 12l-4 4 4 4"),
            Map.entry("tag", "M3 12V4h8l10 10-8 8L3 12z M7.5 8.5h.01"),
            Map.entry("users", "M5.5 7.5a3.5 3.5 0 1 0 7 0a3.5 3.5 0 1 0-7 0z M2 20v-1a4 4 0 0 1 4-4h6a4 4 0 0 1 4 4v1 M16 4.3a3.5 3.5 0 0 1 0 6.4 M19 15.3a4 4 0 0 1 3 3.7v1"),
            Map.entry("building", "M4 21V5l8-2v18 M12 8l8 2v11 M2 21h20 M7 9h2 M7 13h2 M7 17h2 M15 14h2 M15 18h2"),
            Map.entry("win-min", "M5 12h14"),
            Map.entry("win-max", "M6 6h12v12H6z"),
            Map.entry("win-restore", "M9 6h9v9 M6 9h9v9H6z"),
            Map.entry("win-close", "M6 6l12 12 M18 6L6 18"),
            Map.entry("chevron", "M6 9l6 6 6-6"),
            Map.entry("grid", "M4 4h7v7H4z M13 4h7v7h-7z M4 13h7v7H4z M13 13h7v7h-7z"));

    private Icons() {
    }

    /** Verilən ölçüdə ikon qaytarır; cssClass adətən "icon" (ağ) və ya "icon icon-dark". */
    public static Node of(String name, double size, String... styleClasses) {
        SVGPath path = new SVGPath();
        path.setContent(PATHS.getOrDefault(name, PATHS.get("grid")));
        path.getStyleClass().add("icon");
        for (String c : styleClasses) path.getStyleClass().add(c);
        double scale = size / 24.0;
        path.setScaleX(scale);
        path.setScaleY(scale);
        // ölçülən sahə: scale yerləşdirməyə təsir etmir, ona görə sabit ölçülü qab
        StackPane box = new StackPane(path);
        box.setMinSize(size, size);
        box.setPrefSize(size, size);
        box.setMaxSize(size, size);
        return box;
    }
}
