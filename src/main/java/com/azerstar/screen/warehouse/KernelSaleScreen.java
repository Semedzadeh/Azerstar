package com.azerstar.screen.warehouse;

import com.azerstar.screen.Menus;
import com.azerstar.util.DataTable;
import com.azerstar.util.Db;
import com.azerstar.util.ListScreen;
import com.azerstar.util.Row;
import com.azerstar.util.Session;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

/** Ləpə satışlarının siyahısı (satış «Hazır ləpə» ekranından edilir). */
public class KernelSaleScreen extends ListScreen {

    public KernelSaleScreen() {
        super("Ləpə satışları", Menus::warehouse, buildTable());
        init();
    }

    private static TableView<Row> buildTable() {
        List<String> cols = new ArrayList<>(List.of("Tarix/saat", "date_time"));
        if (Session.hasManyBranches()) cols.addAll(List.of("Filial", "branch_name"));
        cols.addAll(List.of("Satış növü", "type_text", "Müştəri", "customer_name", "Ləpə S/N", "kernel_id",
                "Ləpə növü", "kernel_type", "Ləpə ölçüsü", "kernel_size", "Miqdar (kq)", "weight_kg",
                "Qiymət (1 kq / AZN)", "unit_price", "Cəm (AZN)", "total_amount", "Qeyd", "note"));
        return DataTable.create(cols.toArray(new String[0]));
    }

    @Override
    protected ObservableList<Row> load() {
        return Db.query("SELECT s.*, b.name AS branch_name, k.kernel_type, k.kernel_size, "
                + "CASE s.sale_type WHEN 'RETAIL' THEN 'Pərakəndə' ELSE 'Topdan' END AS type_text "
                + "FROM kernel_sale s JOIN branch b ON b.branch_id = s.branch_id JOIN kernel_stock k ON k.kernel_id = s.kernel_id "
                + "WHERE 1 = 1" + Session.branchFilter("s.branch_id") + " ORDER BY s.date_time DESC, s.sale_id DESC");
    }
}
