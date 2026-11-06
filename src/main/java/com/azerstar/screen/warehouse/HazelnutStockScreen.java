package com.azerstar.screen.warehouse;

import com.azerstar.screen.Menus;
import com.azerstar.util.DataTable;
import com.azerstar.util.Db;
import com.azerstar.util.ListScreen;
import com.azerstar.util.Row;
import com.azerstar.util.Session;
import com.azerstar.util.Ui;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

/** Qurudulmuş fındıq: anbarda olanlar və qırmanı gözləyənlər. */
public class HazelnutStockScreen extends ListScreen {

    public HazelnutStockScreen() {
        super("Fındıq anbarı", Menus::warehouse, buildTable());
        init(Ui.primary("Qırmaya başla", this::startShelling));
    }

    private static TableView<Row> buildTable() {
        List<String> cols = new ArrayList<>(List.of("Tarix/saat", "date_time"));
        if (Session.hasManyBranches()) cols.addAll(List.of("Filial", "branch_name"));
        cols.addAll(List.of("Qurutma №", "drying_id", "Qurutma yeri", "place", "Vəziyyət", "state",
                "Çıxan (kq)", "weight_kg", "Qalıq (kq)", "remaining_kg"));
        return DataTable.create(cols.toArray(new String[0]));
    }

    @Override
    protected ObservableList<Row> load() {
        return Db.query("SELECT o.*, b.name AS branch_name, COALESCE(t.name, d.location) AS place, "
                + "CASE o.destination WHEN 'WAREHOUSE' THEN 'Anbarda' ELSE 'Qırma gözləyir' END AS state "
                + "FROM drying_output o JOIN drying d ON d.drying_id = o.drying_id LEFT JOIN tank t ON t.tank_id = d.tank_id "
                + "JOIN branch b ON b.branch_id = o.branch_id WHERE o.remaining_kg > 0.0001"
                + Session.branchFilter("o.branch_id") + " ORDER BY o.date_time DESC, o.output_id DESC");
    }

    private void startShelling() {
        Row r = selected();
        if (r != null && ShellingScreen.openNew(r.integer("output_id"))) reload();
    }
}
