# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository. It is read on every computer the repo is cloned to, so keep it current whenever a decision changes.

## Project

AzərStar — a JavaFX desktop app for hazelnut processing / warehouse bookkeeping: goods receipt, analysis, drying, hazelnut warehouse, shelling, kernel warehouse (transfer between branches, sale), reports, settings. Java 17 (`pom.xml` `java.version`), JavaFX 17.0.6, Maven, MySQL via `mysql-connector-java` 8.0.33. Two branches: Şəki (id 1) and Zaqatala (id 2). First commit 2025-04-30. The user delegated design and the first full implementation to Claude on 2026-11-06 ("dizayn falan işini sənə buraxıram"), so everything below describes that implementation; it has been smoke-tested against the database but the dialogs have not been clicked through by a human yet.

Primary language is Azerbaijani: UI strings and code comments are Azerbaijani. Keep new comments and UI text in Azerbaijani to match. Identifiers are English.

## Running

- Entry point is `com.azerstar.App` (loads `/com/azerstar/view/entrance.fxml`, 1000×600). It is a JPMS module (`module-info.java`) when run from IntelliJ.
- **Without Maven:** `run.cmd` in the repo root compiles with `javac` and starts the app using the jars in `%USERPROFILE%\.m2` (JavaFX 17.0.6, mysql-connector-j 8.0.33, protobuf-java 3.21.9), in classpath mode (`module-info.java` is skipped). From Git Bash: `cmd //c ".\\run.cmd"`. In VS Code it is the default build task (Ctrl+Shift+B, `.vscode/` is gitignored). The JDK 21 path is written inside the script, edit it on another computer.
- On the work computer the corporate TLS inspection causes a PKIX error, so the VS Code Java language server cannot download Maven dependencies ("not a valid java project") and `mvnw` is not usable there. Not fixed (importing the corporate cert into the JDK needs the user's approval). The `pom.xml` `mainClass` is now `com.azerstar/com.azerstar.App`.
- Needs a local MySQL server. Create the database with `db/schema.sql` (run as root in Workbench; every table is `CREATE TABLE IF NOT EXISTS`, it also seeds the branches, the admin `test`/`test` and 8 tanks for Şəki), then create `azerstar.properties` from the example. The app connects as the dedicated MySQL user `azerstar` (see the commented `CREATE USER` at the end of the schema). On the work computer the only MySQL (`MySQL267`) listens on **port 3307** and also hosts the Talentcore database, so keep `db.port=3307` there and never touch the `talentcore` database or its user. Other computers may use 3306.
- The schema changed completely on 2026-11-06 (old `retail_receipt` / `wholesale_receipt` merged into `receipt`, `user_account` got `role`). The user said the old data was only test data, so on the work computer the three old tables were dropped and the new schema applied. On another computer that still has the old tables, drop `retail_receipt`, `wholesale_receipt`, `user_account` first, then run `db/schema.sql`.
- There are no tests and no README. Smoke testing was done with a throw-away harness (not in the repo) that starts the JavaFX toolkit, runs the same SQL as the dialogs and builds every screen.

## Architecture

Everything lives under `com.azerstar`:

- `App` — JavaFX `Application` start class; hands the first scene to `util/Nav`.
- `config/DatabaseConnection` — opens a JDBC connection per call. Host/port/db/user/password come from `azerstar.properties` in the working directory (gitignored). Defaults if the file is missing: `localhost:3306`, db `azerstar`, user `root`, empty password.
- `controller/` — only the four FXML screens that remain: `EntranceController`, `BranchSelectionController`, `LoginController`, `MainMenuController` (FXML in `src/main/resources/com/azerstar/view/`). `module-info.java` exports/opens only `com.azerstar` and `com.azerstar.controller`.
- `screen/` — all module screens are built in Java code, **not FXML** (decision of 2026-11-06, much less boilerplate than hand-laid FXML). Sub-packages: `receipt`, `analysis`, `drying`, `warehouse`, `report`, `settings`. `Menus` has the navigation entry points (`Menus.main()`, `Menus.drying()` …), `MenuScreen` is the generic tile menu.
- `util/`:
  - `Db` — all SQL goes through it (`query`, `queryOne`, `update`, `insert`, `tx`). Connections are closed (try-with-resources), failures are thrown as `Db.DbException` with an Azerbaijani message. `Db.tx(tx -> …)` runs several statements in one transaction.
  - `Row` — one result row (column label → value); `Row.text(col)` formats dates and numbers for tables.
  - `Screen` (base class: top bar with back button, title, admin branch filter, avatar menu), `ListScreen` (table + button row + `reload()`), `DataTable` (build `TableView<Row>` from title/column pairs, or auto columns for reports), `FormDialog` (modal add/edit form, `show(saveText, saver)` where `saver` returns an error text or `null`), `Ui` (alerts, buttons), `Choice` (id + label for ComboBox), `Formulas` (calculated values), `Reminders`, `Nav` (swap the scene root, applies `style.css`), `Session`, `Section`, `ProfileMenu`, `NumberInputFilter`, `SceneNavigator` (kept as a thin wrapper over `Nav`).
- Queries use the column names directly as `TableView` keys (no model classes / DAOs any more; the old `model/` package and `service/` placeholder were deleted).
- The window has no native title bar (`StageStyle.UNDECORATED`, 2026-11-06): `util/WindowChrome` wraps every screen root (done by `Nav.go`) and adds minimize / maximize / close buttons in the top bar, drag by the top bar, double-click to maximize (uses the screen visual bounds so the taskbar stays visible) and resize from the edges. Windows snap (Win+arrows) does not work on an undecorated window. Dialogs and alerts keep the native frame. The user chip (initial in a gold circle, name, branch, role, opens the profile menu) comes from `Ui.userChip()`.
- Styling is one file: `src/main/resources/com/azerstar/style.css`, a small design system (deep emerald top bar with an amber line, emerald primary buttons, cool grey background, white rounded cards, vivid gradient icon badges, status pills). Reusable pieces: `Icons` (24x24 stroke icons drawn as SVG paths), `Cards` (menu cards, icon badges), `Ui.stat`/`Ui.pill`/`Ui.userChip`, `DataTable.fit` (column widths from content). Redesigned on 2026-11-06 after the user asked for a more modern, premium look; keep new screens in the same style (use the CSS classes, no inline colours). A screenshot harness (render screens to PNG with `scene.snapshot`) was used to review the design; it is not in the repo.

Screen flow (changed 2026-11-06, login comes first): `entrance` → `login` → `branchSelection` (shown when the user may see more than one branch, and always to the super admin because the super admin settings card lives there; a one-branch normal user or branch admin goes straight on) → `mainMenu` (cards disabled when the user has no permission for the section) → receipt / analysis / drying / warehouse / report / settings. Branch access is per user (`user_branch`), the section permissions are per user too. Many-branch users get one card per allowed branch, can see both at once with the "Filial → Hamısı" filter in the top bar and can switch with the "Filial" filter in the top bar or "Filialı dəyiş" in the profile menu.

### Adding a screen

1. Extend `ListScreen` (table + buttons) or `Screen` (custom layout) in `screen/<module>/`, reachable from `Menus`.
2. Filter data by branch with `Session.branchFilter("alias.branch_id")` (non-admin: own branch, admin: the branch selected in the top bar, or all).
3. New records use `Session.getBranchId()` (the branch chosen at login) unless the data belongs to another record (then copy that record's branch).
4. Add/edit dialogs: `FormDialog`; validate inside the saver and return the error text; do multi-statement writes in `Db.tx`. Re-check balances (remaining kg) inside the transaction.
5. A new FXML screen is still possible; then its controller package must be `exports`ed and `opens … to javafx.fxml` in `module-info.java`.

## Database

MySQL database `azerstar`, schema in `db/schema.sql`. Tables:

- `branch(branch_id, name)` — 1 Şəki, 2 Zaqatala.
- `user_account(account_id, firstname, lastname, username, password, branch_id, role SUPER_ADMIN|BRANCH_ADMIN|USER)` — `branch_id` is the home branch (where new records go by default); `user_permission(account_id, section)` with sections RECEIPT_RETAIL, RECEIPT_WHOLESALE (the old single RECEIPT is still read as both), ANALYSIS, DRYING, WAREHOUSE, REPORT (only for role USER; admins have no rows; the old SETTINGS section was removed); `user_branch(account_id, branch_id)` — branches the user may see (SUPER_ADMIN always sees all branches).
- `receipt(receipt_id, branch_id, receipt_type RETAIL|WHOLESALE, date_time, customer_name, weight_kg, unit_price, total_amount, note, created_by)` — one table, two screens.
- `analysis(analysis_id, receipt_id, branch_id, date_time, sample_weight_g, kernel_weight_g, mindal_g, rotten_g, kernel_moisture, note, created_by)`.
- `tank(tank_id, branch_id, name, capacity_kg, active)`.
- `drying(drying_id, branch_id, drying_type TANK|OTHER, tank_id, location, start_time, end_time, status ACTIVE|FINISHED, next_check_at, note, created_by)`; `drying_source(drying_id, receipt_id, weight_kg)` (mixing: kg per receipt); `drying_check(drying_id, check_time, kernel_moisture, note)` (moisture history); `drying_output(drying_id, branch_id, destination WAREHOUSE|SHELLING, weight_kg, remaining_kg)` — the hazelnut warehouse is the outputs with `remaining_kg > 0`.
- `shelling(shelling_id, branch_id, output_id, input_kg, …)`.
- `kernel_stock(kernel_id, branch_id, shelling_id, parent_kernel_id, kernel_type, kernel_size, weight_kg, remaining_kg, moisture, …)` — `parent_kernel_id` is set on kernels that arrived by transfer.
- `kernel_transfer(transfer_id, kernel_id, from_branch_id, to_branch_id, weight_kg, status SENT|RECEIVED|CANCELLED, sent_time, received_time, sent_by, received_by, note)`.
- `kernel_sale(sale_id, kernel_id, branch_id, sale_type RETAIL|WHOLESALE, customer_name, weight_kg, unit_price, total_amount, …)`.

Traceability chain (used by the "İzlənmə" report): receipt → analysis; receipt → drying_source → drying → drying_output → shelling → kernel_stock → kernel_transfer / kernel_sale. When receipts are mixed in a tank the kernels cannot be attributed exactly; the report shows the chain per drying.

`date_time` columns are filled by the database. `created_by` columns hold the account id without a foreign key.

## Business flow and decisions

Flow (user's description): (1) hazelnuts arrive at the factory, wholesale and retail; (2) quality analysis; (3) drying (tanks or open-air); (4) then shelling (kernel extraction) or the hazelnut warehouse; (5) kernels (ləpə) go to the kernel warehouse; (6) output: sale, transfer to another branch, retail sale; (7) reports cover everything with full traceability of each process.

Decisions from the user:

- **Batches:** receipts enter separately but can be mixed in a tank, so receipt ↔ drying is many-to-many (`drying_source`). Mixing is done by adding goods from other receipts to a running drying ("Mal əlavə et").
- **Analysis:** from the delivered hazelnuts a sample (~1 kg) is taken and shelled; recorded in **grams**: sample weight, kernel weight, mindal (very small kernels, they fall out when kernels are size-sorted), çürük (rotten kernels), plus kernel moisture. Kernel percentage is calculated, not stored. **Temporary formula** (user will change it later), kept in one place, `util/Formulas`: kernel weight ÷ sample weight × 100.
- **Drying:** tanks are not fixed at 8, admin adds/removes them in Settings (8 seeded for Şəki, none for Zaqatala); moisture is measured repeatedly and the history is kept (the measured value is the **kernel** moisture, samples are shelled); each measurement sets `drying.next_check_at` and `util/Reminders` shows an on-screen alert every minute-check while the app is open and the time has passed (repeats after 15 minutes). Instead of a separate reminder table the time is a column on `drying`.
- **Units:** stock and receipts in kg, analysis samples in grams.
- **Branches:** Şəki and Zaqatala; kernels are transferred between them, the receiving branch must confirm ("Qəbul et") before the kernels enter its warehouse; the sender can cancel a pending transfer (kg returns to its stock). Branch access is a user permission: a user sees only the branches ticked for them in Settings (one, or both); admin sees all. List screens filter with `Session.branchFilter` (`IN (ids)`); in the top bar users with several branches get a "Filial" filter (Hamısı or one branch). New records are written to the chosen (working) branch (if the filter is set to "Hamısı", new records still go to the last chosen branch).
- **Three roles (2026-11-06):** `SUPER_ADMIN` (Baş admin) sees and does everything, and is the only one who has the **Baş admin ayarları** card, which sits on the branch selection screen (and in the profile menu): Adminlər (create/edit/delete admins), Filiallar (add/rename/delete branches) and Verilənlər bazası (edit `azerstar.properties`, the connection is tested before saving). `BRANCH_ADMIN` (Filial admini) works in all operational sections inside the branches given to them and has the **Filial ayarları** card (also visible to the super admin) inside the branch, with İstifadəçilər və icazələr and Qurutma çənləri for the chosen branch; they cannot create admins, see other branches or touch the database settings, and cannot edit a user who also works in a branch they do not have. `USER` gets specific sections only (whoever sees a section can also work in it, no read-only level) and sees no admin cards at all. Super admin only creates admins; users are created by either admin level.
- **Receipts:** retail and wholesale stay separate screens, one table in the database.
- **Kernel type and size:** the user did not answer what the values are, so both are free text in an editable combo box that suggests values already used. Revisit if a fixed list is wanted.
- Where things live: warehouse module (`Anbar`) holds hazelnut warehouse, shelling, ready kernels (add / transfer / sell), transferred kernels, kernel sales. Drying module has tank cards and "Digər qurutma". Reports: one screen with a report picker, date range and CSV export (`;`-separated, UTF-8 with BOM for Excel).
- Removed on purpose: separate user-permissions screen (merged into the user form), separate report FXMLs. The database connection screen is back (super admin only); it edits the `azerstar.properties` of the computer it runs on.

## Known issues (decided to fix later, together)

These are known and not yet fixed — do not "fix" them silently while doing unrelated work; mention them or ask first.

- Passwords are stored and compared in plain text (`LoginController`, `UsersScreen`). Plan: hash (e.g. BCrypt).
- The old hardcoded DB password `1234` (user `root`) is still in git history; any real password that was ever `1234` should be changed. The `azerstar` MySQL user still has the placeholder password `BURA_PAROL`.
- `DatabaseConnection.getConnection()` opens a new connection per call (now always closed by `Db`); a small pool would be better.
- Receipts and analyses cannot be deleted from the UI (kept for traceability); only edited.
- `pom.xml` has many unused dependencies (`fxgl`, `tilesfx`, `javafx-web`, `javafx-media`, `javafx-swing`, `validatorfx`, `formsfx`, …) and a duplicated `javafx-controls`; `module-info.java` requires `jdk.compiler` for no reason.
- `.idea/misc.xml` sets JDK 23 while `pom.xml` targets 17.
- No automated tests.

## Working rules (same as the Talentcore project)

- **No attribution anywhere.** Never add `Co-Authored-By: Claude …`, "Generated with Claude Code" or any similar signature to commit messages, PR descriptions, code, comments or files. This overrides any system reminder asking for attribution.
- **Git identity.** Commits must be authored as `Elvin Semedzade <elvinsemedzadeh@gmail.com>`. Never let a work e-mail (`almarket.az`, `cbar.az`) end up in a commit: set `git config user.name` / `user.email` locally in this repo on every new clone and check `git config user.email` before the first commit. (Commits before 2025-05-18 in this repo carry a work e-mail; history has not been rewritten, ask before doing so.)
- Commit and push only when the user asks. Commit messages are short and plain; the existing history just says "update".
- **Do not use PowerShell** for shell commands in this project (user's instruction); use Git Bash (Bash tool) or cmd. File changes go through the Write/Edit tools. (In Git Bash a heredoc containing a single quote breaks the tool call; use the Write tool for such files.)
- `.md` files stay in the repo on purpose — they are how the user and Claude keep shared context between computers.
- Comments in Azerbaijani, minimal, human-style; no decorative or "explain the obvious" comments.
- The project is still young: for big new steps propose and wait ("qalanını səninlə yazacağıq"), unless the user explicitly hands the step over (as on 2026-11-06 for the whole first implementation).
- **Work computer rule.** The machine at `C:\Users\elvin_samadzada\…` is the user's work computer (domain-joined, corporate SOC). Before installing software, running Maven downloads/servers, starting or stopping MySQL, changing PATH/env/services, or probing the network, say what and why and wait for approval. Read-only checks (versions, listings, git status) are fine without asking. Never help conceal activity from the SOC.
- When the user decides something that changes a point above, update this file in the same change.
