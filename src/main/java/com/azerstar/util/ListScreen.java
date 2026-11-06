package com.azerstar.util;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Yuxarıda düymələr, altında cədvəl olan ekran. */
public abstract class ListScreen extends Screen {

    protected final TableView<Row> table;
    private final HBox toolbar = new HBox(10);
    private final Label countLabel = new Label();

    protected ListScreen(String title, Runnable onBack, TableView<Row> table) {
        this(title, onBack, table, true);
    }

    /** branchFilter=false: filial filtri göstərilmir (məsələn filialların özünün siyahısında). */
    protected ListScreen(String title, Runnable onBack, TableView<Row> table, boolean branchFilter) {
        super(title, onBack, branchFilter);
        this.table = table;
        toolbar.setAlignment(Pos.CENTER_LEFT);
        countLabel.getStyleClass().add("count-label");
        VBox.setVgrow(table, Priority.ALWAYS);
        setContent(new VBox(14, toolbar, table));
    }

    /** Ekran qurulandan sonra çağırılır: düymələri qoyur və məlumatı yükləyir. */
    protected void init(Node... buttons) {
        toolbar.getChildren().addAll(buttons);
        toolbar.getChildren().add(Ui.spacer());
        toolbar.getChildren().add(countLabel);
        Node refresh = Ui.button("Yenilə", this::reload);
        refresh.getStyleClass().add("ghost-button");
        toolbar.getChildren().add(refresh);
        reload();
    }

    protected abstract ObservableList<Row> load();

    @Override
    protected void reload() {
        Ui.run(() -> {
            ObservableList<Row> rows = load();
            table.setItems(rows);
            DataTable.refit(table);
            countLabel.setText(rows.size() + " qeyd");
        });
    }

    /** Seçilmiş sətri qaytarır, seçilməyibsə xəbərdarlıq göstərir. */
    protected Row selected() {
        Row r = table.getSelectionModel().getSelectedItem();
        if (r == null) Ui.warn("Əvvəlcə cədvəldən sətir seçin.");
        return r;
    }
}
