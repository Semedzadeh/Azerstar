-- AzərStar verilənlər bazası sxemi (MySQL 8+)
-- Workbench-də root ilə icra edin. Hər cədvəl yalnız yoxdursa yaradılır.
-- Təmiz quraşdırma üçün nəzərdə tutulub; köhnə cədvəl strukturunu dəyişmir.

CREATE DATABASE IF NOT EXISTS azerstar
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE azerstar;

-- Filiallar
CREATE TABLE IF NOT EXISTS branch (
    branch_id INT PRIMARY KEY,
    name      VARCHAR(100) NOT NULL UNIQUE
);

-- İstifadəçilər (parol hələlik açıq mətndir, bax: CLAUDE.md)
CREATE TABLE IF NOT EXISTS user_account (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    firstname  VARCHAR(100) NOT NULL,
    lastname   VARCHAR(100) NOT NULL,
    username   VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    branch_id  INT NOT NULL,
    role       ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER',
    CONSTRAINT fk_user_branch FOREIGN KEY (branch_id) REFERENCES branch (branch_id)
);

-- Bölmə icazələri: RECEIPT, ANALYSIS, DRYING, WAREHOUSE, REPORT, SETTINGS
-- Bölməni görən orada işləyə də bilir. Admin üçün sətir lazım deyil.
CREATE TABLE IF NOT EXISTS user_permission (
    account_id INT NOT NULL,
    section    VARCHAR(30) NOT NULL,
    PRIMARY KEY (account_id, section),
    CONSTRAINT fk_perm_user FOREIGN KEY (account_id) REFERENCES user_account (account_id) ON DELETE CASCADE
);

-- İstifadəçinin görə bildiyi filiallar (admin həmişə hamısını görür). user_account.branch_id əsas filialdır
CREATE TABLE IF NOT EXISTS user_branch (
    account_id INT NOT NULL,
    branch_id  INT NOT NULL,
    PRIMARY KEY (account_id, branch_id),
    CONSTRAINT fk_ub_user   FOREIGN KEY (account_id) REFERENCES user_account (account_id) ON DELETE CASCADE,
    CONSTRAINT fk_ub_branch FOREIGN KEY (branch_id)  REFERENCES branch (branch_id)
);

-- Mal qəbulu: pərakəndə və topdan bir cədvəldə, növ sütunu ilə
CREATE TABLE IF NOT EXISTS receipt (
    receipt_id    INT AUTO_INCREMENT PRIMARY KEY,
    branch_id     INT NOT NULL,
    receipt_type  ENUM('RETAIL','WHOLESALE') NOT NULL,
    date_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    customer_name VARCHAR(150) NOT NULL,
    weight_kg     DOUBLE NOT NULL DEFAULT 0,
    unit_price    DOUBLE NOT NULL DEFAULT 0,
    total_amount  DOUBLE NOT NULL DEFAULT 0,
    note          VARCHAR(500) NULL,
    created_by    INT NULL,
    CONSTRAINT fk_receipt_branch FOREIGN KEY (branch_id) REFERENCES branch (branch_id)
);

-- Analiz: nümunə çəkiləri qramla; hər mal qəbuluna bir analiz
CREATE TABLE IF NOT EXISTS analysis (
    analysis_id      INT AUTO_INCREMENT PRIMARY KEY,
    receipt_id       INT NOT NULL,
    branch_id        INT NOT NULL,
    date_time        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sample_weight_g  DOUBLE NOT NULL,
    kernel_weight_g  DOUBLE NOT NULL DEFAULT 0,
    mindal_g         DOUBLE NOT NULL DEFAULT 0,
    rotten_g         DOUBLE NOT NULL DEFAULT 0,
    kernel_moisture  DOUBLE NULL,
    note             VARCHAR(500) NULL,
    created_by       INT NULL,
    UNIQUE KEY uq_analysis_receipt (receipt_id),
    CONSTRAINT fk_analysis_receipt FOREIGN KEY (receipt_id) REFERENCES receipt (receipt_id),
    CONSTRAINT fk_analysis_branch  FOREIGN KEY (branch_id)  REFERENCES branch (branch_id)
);

-- Qurutma çənləri (ayarlardan artırılıb azaldılır)
CREATE TABLE IF NOT EXISTS tank (
    tank_id     INT AUTO_INCREMENT PRIMARY KEY,
    branch_id   INT NOT NULL,
    name        VARCHAR(100) NOT NULL,
    capacity_kg DOUBLE NULL,
    active      TINYINT(1) NOT NULL DEFAULT 1,
    UNIQUE KEY uq_tank_name (branch_id, name),
    CONSTRAINT fk_tank_branch FOREIGN KEY (branch_id) REFERENCES branch (branch_id)
);

-- Qurutma partiyası: çəndə (TANK) və ya başqa yerdə (OTHER)
CREATE TABLE IF NOT EXISTS drying (
    drying_id     INT AUTO_INCREMENT PRIMARY KEY,
    branch_id     INT NOT NULL,
    drying_type   ENUM('TANK','OTHER') NOT NULL,
    tank_id       INT NULL,
    location      VARCHAR(150) NULL,
    start_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time      DATETIME NULL,
    status        ENUM('ACTIVE','FINISHED') NOT NULL DEFAULT 'ACTIVE',
    next_check_at DATETIME NULL,
    note          VARCHAR(500) NULL,
    created_by    INT NULL,
    CONSTRAINT fk_drying_branch FOREIGN KEY (branch_id) REFERENCES branch (branch_id),
    CONSTRAINT fk_drying_tank   FOREIGN KEY (tank_id)   REFERENCES tank (tank_id)
);

-- Qarışma: hər qəbuldan bu qurutmaya neçə kq girdi
CREATE TABLE IF NOT EXISTS drying_source (
    source_id  INT AUTO_INCREMENT PRIMARY KEY,
    drying_id  INT NOT NULL,
    receipt_id INT NOT NULL,
    weight_kg  DOUBLE NOT NULL,
    added_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dsrc_drying  FOREIGN KEY (drying_id)  REFERENCES drying (drying_id) ON DELETE CASCADE,
    CONSTRAINT fk_dsrc_receipt FOREIGN KEY (receipt_id) REFERENCES receipt (receipt_id)
);

-- Qurutma zamanı nəmlik ölçmələri (tarixçə saxlanır)
CREATE TABLE IF NOT EXISTS drying_check (
    check_id        INT AUTO_INCREMENT PRIMARY KEY,
    drying_id       INT NOT NULL,
    check_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    kernel_moisture DOUBLE NOT NULL,
    note            VARCHAR(500) NULL,
    created_by      INT NULL,
    CONSTRAINT fk_dcheck_drying FOREIGN KEY (drying_id) REFERENCES drying (drying_id) ON DELETE CASCADE
);

-- Qurutmadan çıxan fındıq: anbara (WAREHOUSE) və ya qırmaya (SHELLING)
-- remaining_kg qırmaya götürüldükcə azalır
CREATE TABLE IF NOT EXISTS drying_output (
    output_id    INT AUTO_INCREMENT PRIMARY KEY,
    drying_id    INT NOT NULL,
    branch_id    INT NOT NULL,
    destination  ENUM('WAREHOUSE','SHELLING') NOT NULL,
    weight_kg    DOUBLE NOT NULL,
    remaining_kg DOUBLE NOT NULL,
    date_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dout_drying FOREIGN KEY (drying_id) REFERENCES drying (drying_id),
    CONSTRAINT fk_dout_branch FOREIGN KEY (branch_id) REFERENCES branch (branch_id)
);

-- Qırma (ləpənin çıxarılması)
CREATE TABLE IF NOT EXISTS shelling (
    shelling_id INT AUTO_INCREMENT PRIMARY KEY,
    branch_id   INT NOT NULL,
    output_id   INT NOT NULL,
    input_kg    DOUBLE NOT NULL,
    date_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    note        VARCHAR(500) NULL,
    created_by  INT NULL,
    CONSTRAINT fk_shell_branch FOREIGN KEY (branch_id) REFERENCES branch (branch_id),
    CONSTRAINT fk_shell_output FOREIGN KEY (output_id) REFERENCES drying_output (output_id)
);

-- Ləpə anbarı. parent_kernel_id transferlə gələn ləpənin mənbəyini göstərir
CREATE TABLE IF NOT EXISTS kernel_stock (
    kernel_id        INT AUTO_INCREMENT PRIMARY KEY,
    branch_id        INT NOT NULL,
    shelling_id      INT NULL,
    parent_kernel_id INT NULL,
    kernel_type      VARCHAR(80) NULL,
    kernel_size      VARCHAR(80) NULL,
    weight_kg        DOUBLE NOT NULL,
    remaining_kg     DOUBLE NOT NULL,
    moisture         DOUBLE NULL,
    date_time        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    note             VARCHAR(500) NULL,
    created_by       INT NULL,
    CONSTRAINT fk_kernel_branch  FOREIGN KEY (branch_id)        REFERENCES branch (branch_id),
    CONSTRAINT fk_kernel_shell   FOREIGN KEY (shelling_id)      REFERENCES shelling (shelling_id),
    CONSTRAINT fk_kernel_parent  FOREIGN KEY (parent_kernel_id) REFERENCES kernel_stock (kernel_id)
);

-- Filiallararası transfer: göndərilir (SENT), qəbul təsdiqlənir (RECEIVED) və ya ləğv olunur (CANCELLED)
CREATE TABLE IF NOT EXISTS kernel_transfer (
    transfer_id    INT AUTO_INCREMENT PRIMARY KEY,
    kernel_id      INT NOT NULL,
    from_branch_id INT NOT NULL,
    to_branch_id   INT NOT NULL,
    weight_kg      DOUBLE NOT NULL,
    status         ENUM('SENT','RECEIVED','CANCELLED') NOT NULL DEFAULT 'SENT',
    sent_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    received_time  DATETIME NULL,
    sent_by        INT NULL,
    received_by    INT NULL,
    note           VARCHAR(500) NULL,
    CONSTRAINT fk_tr_kernel FOREIGN KEY (kernel_id)      REFERENCES kernel_stock (kernel_id),
    CONSTRAINT fk_tr_from   FOREIGN KEY (from_branch_id) REFERENCES branch (branch_id),
    CONSTRAINT fk_tr_to     FOREIGN KEY (to_branch_id)   REFERENCES branch (branch_id)
);

-- Ləpə satışı (pərakəndə və ya topdan)
CREATE TABLE IF NOT EXISTS kernel_sale (
    sale_id       INT AUTO_INCREMENT PRIMARY KEY,
    kernel_id     INT NOT NULL,
    branch_id     INT NOT NULL,
    sale_type     ENUM('RETAIL','WHOLESALE') NOT NULL,
    customer_name VARCHAR(150) NOT NULL,
    weight_kg     DOUBLE NOT NULL,
    unit_price    DOUBLE NOT NULL DEFAULT 0,
    total_amount  DOUBLE NOT NULL DEFAULT 0,
    date_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    note          VARCHAR(500) NULL,
    created_by    INT NULL,
    CONSTRAINT fk_sale_kernel FOREIGN KEY (kernel_id) REFERENCES kernel_stock (kernel_id),
    CONSTRAINT fk_sale_branch FOREIGN KEY (branch_id) REFERENCES branch (branch_id)
);

-- İlkin məlumatlar
INSERT IGNORE INTO branch (branch_id, name) VALUES (1, 'Şəki'), (2, 'Zaqatala');

-- Yalnız lokal sınaq üçün admin istifadəçi
INSERT IGNORE INTO user_account (firstname, lastname, username, password, branch_id, role)
VALUES ('Test', 'İstifadəçi', 'test', 'test', 1, 'ADMIN');

INSERT IGNORE INTO user_branch (account_id, branch_id) SELECT account_id, branch_id FROM user_account WHERE username = 'test';

-- Şəki filialında 8 sabit çən (artırıb azaltmaq ayarlardan edilir)
INSERT IGNORE INTO tank (branch_id, name)
VALUES (1, 'Çən 1'), (1, 'Çən 2'), (1, 'Çən 3'), (1, 'Çən 4'),
       (1, 'Çən 5'), (1, 'Çən 6'), (1, 'Çən 7'), (1, 'Çən 8');

-- Tətbiqin qoşulacağı ayrıca istifadəçi (parolu özünüz yazın, azerstar.properties-ə də eyni parolu qoyun)
-- CREATE USER 'azerstar'@'localhost' IDENTIFIED BY 'BURA_PAROL';
-- GRANT ALL PRIVILEGES ON azerstar.* TO 'azerstar'@'localhost';
-- FLUSH PRIVILEGES;
