package com.azerstar.screen.drying;

import com.azerstar.screen.Menus;
import com.azerstar.util.DataTable;
import com.azerstar.util.Db;
import com.azerstar.util.ListScreen;
import com.azerstar.util.Row;
import com.azerstar.util.Session;
import com.azerstar.util.Ui;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;

/** Çənsiz (açıq yerdə və s.) qurutmalar. */
public class OtherDryingScreen extends ListScreen {

    public OtherDryingScreen() {
        super("Digər qurutma", Menus::drying, buildTable());
        table.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) open();
        });
        init(Ui.primary("Yeni qurutma", this::start),
                Ui.button("Aç", this::open));
    }

    private static TableView<Row> buildTable() {
        return DataTable.create("№", "drying_id", "Qurutma yeri / növü", "location", "Başlama", "start_time",
                "Cəmi (kq)", "total_kg", "Son nəmlik (%)", "last_moisture", "Növbəti yoxlama", "next_check_at",
                "Vəziyyət", "status_text", "Qeyd", "note");
    }

    @Override
    protected ObservableList<Row> load() {
        return Db.query("SELECT d.*, CASE d.status WHEN 'ACTIVE' THEN 'Davam edir' ELSE 'Bitib' END AS status_text, "
                + "(SELECT COALESCE(SUM(s.weight_kg), 0) FROM drying_source s WHERE s.drying_id = d.drying_id) AS total_kg, "
                + "(SELECT c.kernel_moisture FROM drying_check c WHERE c.drying_id = d.drying_id ORDER BY c.check_time DESC, c.check_id DESC LIMIT 1) AS last_moisture "
                + "FROM drying d WHERE d.drying_type = 'OTHER'" + Session.branchFilter("d.branch_id")
                + " ORDER BY d.status, d.start_time DESC");
    }

    private void start() {
        int id = DryingForms.start(null, null, Session.getBranchId());
        if (id > 0) new DryingDetailScreen(id, "Digər qurutma", () -> new OtherDryingScreen().show()).show();
    }

    private void open() {
        Row r = selected();
        if (r != null) {
            new DryingDetailScreen(r.integer("drying_id"), r.str("location"), () -> new OtherDryingScreen().show()).show();
        }
    }
}
