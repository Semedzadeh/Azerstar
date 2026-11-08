package com.azerstar.screen.drying;

import com.azerstar.util.Choice;
import com.azerstar.util.Db;
import com.azerstar.util.FormDialog;
import com.azerstar.util.Row;
import com.azerstar.util.Session;
import com.azerstar.util.Ui;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Qurutma ekranlarında istifadə olunan ortaq pəncərələr. */
final class DryingForms {

    private static final String AVAILABLE_SQL =
            "SELECT * FROM (SELECT r.receipt_id, r.customer_name, r.date_time, "
                    + "r.weight_kg - COALESCE((SELECT SUM(s.weight_kg) FROM drying_source s WHERE s.receipt_id = r.receipt_id), 0) AS available_kg "
                    + "FROM receipt r WHERE r.branch_id = ? AND EXISTS (SELECT 1 FROM analysis a WHERE a.receipt_id = r.receipt_id)) x WHERE x.available_kg > 0.0001 ORDER BY x.date_time DESC";

    private DryingForms() {
    }

    /** Qurutmanın növbəti yoxlama vaxtını təyin etmək üçün dəqiqə; sahə boşdursa null. */
    private static Integer minutes(TextField hours) {
        Double h = Ui.number(hours);
        return h == null ? null : (int) Math.round(h * 60);
    }

    private static double availableOf(int receiptId) {
        Row r = Db.queryOne("SELECT r.weight_kg - COALESCE((SELECT SUM(s.weight_kg) FROM drying_source s WHERE s.receipt_id = r.receipt_id), 0) AS available_kg "
                + "FROM receipt r WHERE r.receipt_id = ? AND EXISTS (SELECT 1 FROM analysis a WHERE a.receipt_id = r.receipt_id)", receiptId);
        return r == null ? 0 : r.number("available_kg");
    }

    /** Yeni qurutma başladır. Uğurlu olarsa yeni qurutmanın nömrəsini, əks halda 0 qaytarır. */
    static int start(Integer tankId, String tankName, int branchId) {
        List<Row> available = Db.query(AVAILABLE_SQL, branchId);
        if (available.isEmpty()) {
            Ui.warn("Qurutmaya verilə biləcək mal yoxdur. Mal qəbulu analizdən keçməyibsə, əvvəlcə analiz edin (və ya qalıq bitib).");
            return 0;
        }
        boolean tank = tankId != null;
        FormDialog f = new FormDialog(tank ? "Qurutmanı başlat: " + tankName : "Yeni qurutma");
        TextField location = tank ? null : f.text("Qurutma yeri / növü");
        Map<Integer, Double> availableKg = new HashMap<>();
        ComboBox<Choice> receipt = f.combo("Mal qəbulu", choicesOf(available, availableKg));
        receipt.setPrefWidth(380);
        TextField kg = f.number("Qurutmaya verilən miqdar (kq)");
        TextField hours = f.number("Növbəti nəmlik yoxlaması (neçə saat sonra)");
        hours.setText("12");
        TextField note = f.text("Qeyd");
        f.addInfo("Sonra eyni qurutmaya başqa qəbullardan da mal əlavə edə bilərsiniz (qarışdırma).");

        receipt.valueProperty().addListener((o, a, b) -> {
            if (b != null) kg.setText(Row.format(availableKg.get(b.id())));
        });

        int[] created = {0};
        f.show("Başlat", () -> {
            if (!tank && location.getText().isBlank()) return "Qurutma yerini yazın (məsələn: açıq meydança).";
            if (receipt.getValue() == null) return "Mal qəbulunu seçin.";
            Double w = Ui.number(kg);
            if (w == null || w <= 0) return "Miqdarı (kq) daxil edin.";
            if (w > availableOf(receipt.getValue().id()) + 0.0001) return "Bu qəbulda bu qədər boş mal yoxdur.";
            if (tank) {
                Row busy = Db.queryOne("SELECT COUNT(*) AS n FROM drying WHERE tank_id = ? AND status = 'ACTIVE'", tankId);
                if (busy.integer("n") > 0) return "Bu çəndə artıq qurutma gedir.";
            }
            Integer mins = minutes(hours);
            int receiptId = receipt.getValue().id();
            created[0] = Db.tx(tx -> {
                long id = tx.insert("INSERT INTO drying (branch_id, drying_type, tank_id, location, next_check_at, note, created_by) "
                                + "VALUES (?, ?, ?, ?, DATE_ADD(NOW(), INTERVAL ? MINUTE), ?, ?)",
                        branchId, tank ? "TANK" : "OTHER", tankId, tank ? null : location.getText().trim(),
                        mins, note.getText().trim(), Session.getAccountId());
                tx.insert("INSERT INTO drying_source (drying_id, receipt_id, weight_kg) VALUES (?, ?, ?)", id, receiptId, w);
                return (int) id;
            });
            return null;
        });
        return created[0];
    }

    /** Işləyən qurutmaya başqa qəbuldan mal əlavə edir (qarışdırma). */
    static boolean addSource(int dryingId, int branchId) {
        List<Row> available = Db.query(AVAILABLE_SQL, branchId);
        if (available.isEmpty()) {
            Ui.warn("Əlavə ediləcək boş mal yoxdur.");
            return false;
        }
        FormDialog f = new FormDialog("Qurutmaya mal əlavə et");
        Map<Integer, Double> availableKg = new HashMap<>();
        ComboBox<Choice> receipt = f.combo("Mal qəbulu", choicesOf(available, availableKg));
        receipt.setPrefWidth(380);
        TextField kg = f.number("Miqdar (kq)");
        receipt.valueProperty().addListener((o, a, b) -> {
            if (b != null) kg.setText(Row.format(availableKg.get(b.id())));
        });
        return f.show("Əlavə et", () -> {
            if (receipt.getValue() == null) return "Mal qəbulunu seçin.";
            Double w = Ui.number(kg);
            if (w == null || w <= 0) return "Miqdarı (kq) daxil edin.";
            if (w > availableOf(receipt.getValue().id()) + 0.0001) return "Bu qəbulda bu qədər boş mal yoxdur.";
            Db.insert("INSERT INTO drying_source (drying_id, receipt_id, weight_kg) VALUES (?, ?, ?)", dryingId, receipt.getValue().id(), w);
            return null;
        });
    }

    /** Nəmlik ölçməsi: ləpənin nəmliyi yazılır, növbəti yoxlama vaxtı təyin olunur. */
    static boolean addCheck(int dryingId) {
        FormDialog f = new FormDialog("Nəmlik ölçməsi");
        f.addInfo("Fındıqdan nümunə götürülüb qırılır və ləpənin nəmliyi ölçülür.");
        TextField moisture = f.number("Ləpənin nəmliyi (%)");
        TextField hours = f.number("Növbəti yoxlama (neçə saat sonra)");
        hours.setText("6");
        TextField note = f.text("Qeyd");
        return f.show("Yaz", () -> {
            Double m = Ui.number(moisture);
            if (m == null) return "Ləpənin nəmliyini daxil edin.";
            Integer mins = minutes(hours);
            Db.tx(tx -> {
                tx.insert("INSERT INTO drying_check (drying_id, kernel_moisture, note, created_by) VALUES (?, ?, ?, ?)",
                        dryingId, m, note.getText().trim(), Session.getAccountId());
                tx.update("UPDATE drying SET next_check_at = DATE_ADD(NOW(), INTERVAL ? MINUTE) WHERE drying_id = ?", mins, dryingId);
                return null;
            });
            return null;
        });
    }

    /** Qurutmanı bitirir və çıxan fındığı anbara / qırmaya yönləndirir. */
    static boolean finish(int dryingId, int branchId, double totalKg) {
        FormDialog f = new FormDialog("Qurutmanı bitir");
        f.addInfo("Qurutmaya verilən cəmi: " + Row.format(totalKg) + " kq. Qurudulmuş fındığın hara getdiyini yazın.");
        TextField toWarehouse = f.number("Fındıq anbarına (kq)");
        TextField toShelling = f.number("Qırmaya (kq)");
        return f.show("Bitir", () -> {
            double a = nz(Ui.number(toWarehouse));
            double b = nz(Ui.number(toShelling));
            if (a + b <= 0) return "Ən azı bir yerə miqdar yazın.";
            if (a + b > totalKg + 0.0001) return "Çıxan miqdar qurutmaya verilən miqdardan çox ola bilməz.";
            Db.tx(tx -> {
                if (a > 0) {
                    tx.insert("INSERT INTO drying_output (drying_id, branch_id, destination, weight_kg, remaining_kg) VALUES (?, ?, 'WAREHOUSE', ?, ?)",
                            dryingId, branchId, a, a);
                }
                if (b > 0) {
                    tx.insert("INSERT INTO drying_output (drying_id, branch_id, destination, weight_kg, remaining_kg) VALUES (?, ?, 'SHELLING', ?, ?)",
                            dryingId, branchId, b, b);
                }
                tx.update("UPDATE drying SET status = 'FINISHED', end_time = NOW(), next_check_at = NULL WHERE drying_id = ?", dryingId);
                return null;
            });
            return null;
        });
    }

    private static List<Choice> choicesOf(List<Row> rows, Map<Integer, Double> availableKg) {
        List<Choice> list = new ArrayList<>();
        for (Row r : rows) {
            availableKg.put(r.integer("receipt_id"), r.number("available_kg"));
            list.add(new Choice(r.integer("receipt_id"), "№" + r.integer("receipt_id") + " · " + r.str("customer_name")
                    + " · boş: " + r.text("available_kg") + " kq · " + r.text("date_time")));
        }
        return list;
    }

    private static double nz(Double d) {
        return d == null ? 0 : d;
    }
}
