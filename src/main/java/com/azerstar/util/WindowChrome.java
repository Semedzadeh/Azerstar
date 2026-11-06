package com.azerstar.util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Standart Windows başlıq zolağı əvəzinə tətbiqin öz yuxarı panelində pəncərə idarəsi:
 * sürüşdürmək, iki dəfə basıb böyütmək, kiçilt / böyüt / bağla düymələri və kənarlardan ölçü dəyişmə.
 */
public final class WindowChrome {

    private static final double EDGE = 6;
    private static final double BUTTON_WIDTH = 46;
    private static final double CONTROLS_WIDTH = BUTTON_WIDTH * 3;

    private static final BooleanProperty MAXIMIZED = new SimpleBooleanProperty(false);
    private static double[] restoreBounds;

    private WindowChrome() {
    }

    /** Ekranın kökünü pəncərə idarəsi ilə birlikdə bükür. */
    public static Parent wrap(Parent content) {
        Node topBar = content.lookup(".top-bar");
        boolean lightIcons = topBar != null || content.getStyleClass().contains("hero");

        StackPane shell = new StackPane(content);
        shell.getStyleClass().add("window-shell");
        MAXIMIZED.addListener((o, a, b) -> shell.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("maximized"), b));
        shell.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("maximized"), MAXIMIZED.get());

        HBox controls = controls(lightIcons);
        StackPane.setAlignment(controls, Pos.TOP_RIGHT);
        controls.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        if (topBar instanceof Region bar) {
            controls.prefHeightProperty().bind(bar.heightProperty());
            // düymələrin altında qalmasın deyə yuxarı panelin sağına boşluq
            if (!Boolean.TRUE.equals(bar.getProperties().get("chrome-padded"))) {
                Insets p = bar.getPadding();
                bar.setPadding(new Insets(p.getTop(), p.getRight() + CONTROLS_WIDTH, p.getBottom(), p.getLeft()));
                bar.getProperties().put("chrome-padded", true);
            }
        } else {
            controls.setPrefHeight(40);
        }
        shell.getChildren().add(controls);

        installDrag(shell, topBar);
        installResize(shell);
        return shell;
    }

    private static HBox controls(boolean lightIcons) {
        String[] iconClasses = lightIcons ? new String[0] : new String[]{"icon-dark"};

        Button minimize = windowButton(icon("win-min", iconClasses), () -> Nav.stage().setIconified(true));

        StackPane maxIcons = new StackPane(icon("win-max", iconClasses), icon("win-restore", iconClasses));
        maxIcons.getChildren().get(0).visibleProperty().bind(MAXIMIZED.not());
        maxIcons.getChildren().get(1).visibleProperty().bind(MAXIMIZED);
        Button maximize = windowButton(maxIcons, WindowChrome::toggleMaximize);

        Button close = windowButton(icon("win-close", iconClasses), () -> Nav.stage().close());
        close.getStyleClass().add("win-close");

        HBox box = new HBox(minimize, maximize, close);
        box.getStyleClass().add("win-controls");
        box.setAlignment(Pos.TOP_RIGHT);
        if (!lightIcons) box.getStyleClass().add("win-dark");
        return box;
    }

    private static Node icon(String name, String[] extra) {
        return Icons.of(name, 14, extra);
    }

    private static Button windowButton(Node graphic, Runnable action) {
        Button b = new Button();
        b.setGraphic(graphic);
        b.getStyleClass().add("win-button");
        b.setFocusTraversable(false);
        b.setPrefWidth(BUTTON_WIDTH);
        b.setMinWidth(BUTTON_WIDTH);
        b.setMaxHeight(Double.MAX_VALUE);
        b.setOnAction(e -> action.run());
        return b;
    }

    /** Tapşırıq panelini örtməmək üçün ekranın görünən sahəsinə görə böyüdür. */
    public static void toggleMaximize() {
        Stage s = Nav.stage();
        if (!MAXIMIZED.get()) {
            restoreBounds = new double[]{s.getX(), s.getY(), s.getWidth(), s.getHeight()};
            Rectangle2D area = javafx.stage.Screen.getScreensForRectangle(s.getX(), s.getY(), s.getWidth(), s.getHeight())
                    .get(0).getVisualBounds();
            s.setX(area.getMinX());
            s.setY(area.getMinY());
            s.setWidth(area.getWidth());
            s.setHeight(area.getHeight());
            MAXIMIZED.set(true);
        } else {
            if (restoreBounds != null) {
                s.setX(restoreBounds[0]);
                s.setY(restoreBounds[1]);
                s.setWidth(restoreBounds[2]);
                s.setHeight(restoreBounds[3]);
            }
            MAXIMIZED.set(false);
        }
    }

    // ---------- sürüşdürmə ----------

    private static void installDrag(StackPane shell, Node dragArea) {
        double[] offset = new double[2];
        boolean[] dragging = {false};

        shell.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            dragging[0] = false;
            if (e.getButton() != MouseButton.PRIMARY || !canDrag(e, shell, dragArea)) return;
            if (e.getClickCount() == 2) {
                toggleMaximize();
                return;
            }
            if (MAXIMIZED.get()) return;
            Stage s = Nav.stage();
            offset[0] = e.getScreenX() - s.getX();
            offset[1] = e.getScreenY() - s.getY();
            dragging[0] = true;
        });
        shell.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            if (!dragging[0]) return;
            Nav.stage().setX(e.getScreenX() - offset[0]);
            Nav.stage().setY(e.getScreenY() - offset[1]);
        });
        shell.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> dragging[0] = false);
    }

    private static boolean canDrag(MouseEvent e, StackPane shell, Node dragArea) {
        if (edgeAt(e, shell) != 0) return false;
        if (!(e.getTarget() instanceof Node target)) return false;
        boolean insideArea = dragArea == null;
        for (Node n = target; n != null && n != shell; n = n.getParent()) {
            if (n == dragArea) insideArea = true;
            if (isInteractive(n)) return false;
        }
        return insideArea;
    }

    private static boolean isInteractive(Node n) {
        if (n.getOnMouseClicked() != null) return true;
        if (n instanceof ButtonBase || n instanceof TextInputControl || n instanceof ComboBoxBase || n instanceof ScrollBar) return true;
        return n instanceof Control && !(n instanceof Label);
    }

    // ---------- ölçü dəyişmə ----------

    private static final int LEFT = 1, RIGHT = 2, TOP = 4, BOTTOM = 8;

    private static int edgeAt(MouseEvent e, StackPane shell) {
        if (MAXIMIZED.get()) return 0;
        double x = e.getSceneX(), y = e.getSceneY();
        double w = shell.getScene().getWidth(), h = shell.getScene().getHeight();
        int edge = 0;
        if (x < EDGE) edge |= LEFT;
        if (x > w - EDGE) edge |= RIGHT;
        if (y < EDGE) edge |= TOP;
        if (y > h - EDGE) edge |= BOTTOM;
        return edge;
    }

    private static Cursor cursorFor(int edge) {
        return switch (edge) {
            case LEFT, RIGHT -> Cursor.H_RESIZE;
            case TOP, BOTTOM -> Cursor.V_RESIZE;
            case LEFT | TOP, RIGHT | BOTTOM -> Cursor.NW_RESIZE;
            case RIGHT | TOP, LEFT | BOTTOM -> Cursor.NE_RESIZE;
            default -> Cursor.DEFAULT;
        };
    }

    private static void installResize(StackPane shell) {
        int[] edge = {0};
        double[] start = new double[6];

        shell.addEventFilter(MouseEvent.MOUSE_MOVED, e -> shell.setCursor(cursorFor(edgeAt(e, shell))));
        shell.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            edge[0] = edgeAt(e, shell);
            if (edge[0] == 0) return;
            Stage s = Nav.stage();
            start[0] = e.getScreenX();
            start[1] = e.getScreenY();
            start[2] = s.getX();
            start[3] = s.getY();
            start[4] = s.getWidth();
            start[5] = s.getHeight();
            e.consume();
        });
        shell.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            if (edge[0] == 0) return;
            Stage s = Nav.stage();
            double dx = e.getScreenX() - start[0];
            double dy = e.getScreenY() - start[1];
            double minW = s.getMinWidth() > 0 ? s.getMinWidth() : 400;
            double minH = s.getMinHeight() > 0 ? s.getMinHeight() : 300;
            if ((edge[0] & RIGHT) != 0) s.setWidth(Math.max(minW, start[4] + dx));
            if ((edge[0] & BOTTOM) != 0) s.setHeight(Math.max(minH, start[5] + dy));
            if ((edge[0] & LEFT) != 0) {
                double w = Math.max(minW, start[4] - dx);
                s.setX(start[2] + start[4] - w);
                s.setWidth(w);
            }
            if ((edge[0] & TOP) != 0) {
                double h = Math.max(minH, start[5] - dy);
                s.setY(start[3] + start[5] - h);
                s.setHeight(h);
            }
            e.consume();
        });
        shell.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> edge[0] = 0);
    }
}
