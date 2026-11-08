package com.azerstar.screen;

import com.azerstar.screen.MenuScreen.Tile;
import com.azerstar.screen.analysis.AnalysisScreen;
import com.azerstar.screen.drying.OtherDryingScreen;
import com.azerstar.screen.drying.TankDryingScreen;
import com.azerstar.screen.receipt.ReceiptScreen;
import com.azerstar.screen.report.ReportScreen;
import com.azerstar.screen.settings.AdminsScreen;
import com.azerstar.screen.settings.BranchesScreen;
import com.azerstar.screen.settings.DatabaseSettingsScreen;
import com.azerstar.screen.settings.TanksScreen;
import com.azerstar.screen.settings.UsersScreen;
import com.azerstar.screen.warehouse.HazelnutStockScreen;
import com.azerstar.screen.warehouse.KernelSaleScreen;
import com.azerstar.screen.warehouse.KernelTransferScreen;
import com.azerstar.screen.warehouse.ReadyKernelScreen;
import com.azerstar.screen.warehouse.ShellingScreen;
import com.azerstar.util.Db;
import com.azerstar.util.Nav;
import com.azerstar.util.Row;
import com.azerstar.util.Section;
import com.azerstar.util.Session;
import java.util.ArrayList;
import java.util.List;


/** Bütün menyu və ekranlara keçidlər bir yerdə. */
public final class Menus {

    private Menus() {
    }

    public static void main() {
        Nav.goFxml("/com/azerstar/view/mainMenu.fxml");
    }

    public static void receipt() {
        List<Tile> tiles = new ArrayList<>();
        if (Session.can(Section.RECEIPT_RETAIL)) {
            tiles.add(new Tile("Pərakəndə qəbul", "Fərdi müştərilərdən gələn mal", "receipt", "hazel", () -> new ReceiptScreen("RETAIL").show()));
        }
        if (Session.can(Section.RECEIPT_WHOLESALE)) {
            tiles.add(new Tile("Topdan qəbul", "Fermer və şirkətlərdən böyük partiyalar", "package", "slate", () -> new ReceiptScreen("WHOLESALE").show()));
        }
        new MenuScreen("Mal qəbulu", "Fabrikə gələn fındığın qəbulu. Növü seçin.", Menus::main, tiles.toArray(new Tile[0])).show();
    }

    public static void analysis() {
        new AnalysisScreen().show();
    }

    public static void drying() {
        new MenuScreen("Qurutma", "Qurutma üsulunu seçin.", Menus::main,
                new Tile("Çənlər ilə qurutma", "Çənlərin vəziyyəti, nəmlik ölçmələri", "tank", "amber", () -> new TankDryingScreen().show()),
                new Tile("Digər qurutma", "Açıq yerdə və ya çənsiz qurutma", "sun", "gold", () -> new OtherDryingScreen().show())).show();
    }

    public static void warehouse() {
        String transferText = "Transfer edilmiş ləpə";
        String transferSub = "Filiallar arası göndəriş və qəbul";
        try {
            String filter = " AND to_branch_id IN (" + Session.scopeIds() + ")";
            Row r = Db.queryOne("SELECT COUNT(*) AS n FROM kernel_transfer WHERE status = 'SENT'" + filter);
            int pending = r == null ? 0 : r.integer("n");
            if (pending > 0) transferSub = pending + " transfer təsdiq gözləyir";
        } catch (Db.DbException e) {
            // say göstərilmirsə də menyu açılsın
        }
        new MenuScreen("Anbar", "Qurudulmuş fındıq, qırma və hazır ləpə.", Menus::main,
                new Tile("Fındıq anbarı", "Qurudulmuş fındıq qalıqları", "package", "slate", () -> new HazelnutStockScreen().show()),
                new Tile("Qırma", "Fındığın qırılması, ləpə çıxımı", "layers", "hazel", () -> new ShellingScreen().show()),
                new Tile("Hazır ləpə", "Ləpə anbarı: əlavə, transfer, satış", "kernel", "green", () -> new ReadyKernelScreen().show()),
                new Tile(transferText, transferSub, "transfer", "amber", () -> new KernelTransferScreen().show()),
                new Tile("Ləpə satışları", "Pərakəndə və topdan satışlar", "tag", "plum", () -> new KernelSaleScreen().show())).show();
    }

    public static void report() {
        new ReportScreen().show();
    }

    /** Filial ayarları (filialın daxilində): seçilmiş filialın istifadəçiləri və çənləri. Baş admin və filial admini üçün. */
    public static void branches() {
        new MenuScreen("Filial ayarları", "Seçilmiş filialın istifadəçiləri və qurutma çənləri.", Menus::main,
                new Tile("İstifadəçilər və icazələr", "İstifadəçilər, filial və bölmə icazələri", "users", "slate", () -> new UsersScreen().show()),
                new Tile("Qurutma çənləri", "Çən əlavə et, tutum, aktiv/passiv", "tank", "amber", () -> new TanksScreen().show())).show();
    }

    /** Filial seçimi ekranı (girişdən sonra). */
    public static void branchSelection() {
        Nav.goFxml("/com/azerstar/view/branchSelection.fxml");
    }

    /** Baş admin ayarları: adminlər, filialların özü, verilənlər bazası bağlantısı. Yalnız baş admin. */
    public static void settings() {
        new MenuScreen("Baş admin ayarları", "Adminlər, filiallar və verilənlər bazası bağlantısı.", Menus::branchSelection,
                new Tile("Adminlər", "Baş admin və filial adminləri", "users", "plum", () -> new AdminsScreen().show()),
                new Tile("Filiallar", "Filial əlavə et, adını dəyiş, sil", "building", "teal", () -> new BranchesScreen().show()),
                new Tile("Verilənlər bazası", "Server, port, istifadəçi, şifrə", "sliders", "gray", () -> new DatabaseSettingsScreen().show())).show();
    }
}
