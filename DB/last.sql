-- ERP 系統資料庫架構初始化腳本
-- 版本：1.1.4
-- 說明：在空的容器資料庫中直接建立最新版本的資料庫架構

-- 使用 erp_db 數據庫
USE erp_db;

-- 開始事務
START TRANSACTION;

-- ========================================
-- 第一部分：基礎配置表 (版本 1.1.1)
-- ========================================

-- 創建 key_values 表 - 系統配置參數存儲表
CREATE TABLE IF NOT EXISTS key_values
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主鍵',
    `key`         VARCHAR(255) NOT NULL COMMENT '配置名稱',
    `value`       TEXT NULL COMMENT '值',
    `description` VARCHAR(255) NULL COMMENT '說明',
    `created_at`  DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '創建時間',
    `updated_at`  DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新時間',
    INDEX         idx_key (`key`)
) COMMENT = '系統配置參數存儲表';

-- ========================================
-- 第二部分：商品與庫存相關表 (版本 1.1.2)
-- ========================================

-- 創建 items 表 - 商品基本資料表
CREATE TABLE IF NOT EXISTS items
(
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品唯一識別碼，自增',
    `name`       VARCHAR(100) NULL COMMENT '商品名稱',
    `unit`       VARCHAR(20) NULL COMMENT '單位（例如個、箱）',
    `created_at` DATETIME(3) NULL COMMENT '建立時間（毫秒級）',
    `updated_at` DATETIME(3) NULL COMMENT '更新時間（毫秒級）'
) COMMENT = '商品基本資料表';

-- 創建 stocks 表 - 商品庫存狀態表（已包含 1.1.3 版本的修改）
CREATE TABLE IF NOT EXISTS stocks
(
    `id`                    BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '庫存記錄唯一識別碼，自增',
    `item_id`               BIGINT NULL COMMENT '對應商品ID',
    `warehouse_id`          BIGINT NULL COMMENT '倉庫ID',
    `storage_location_id`   BIGINT NULL COMMENT '儲位ID',
    `quantity`              DECIMAL(18,6) NULL COMMENT '現有庫存量',
    `created_at`            DATETIME(3) NULL COMMENT '建立時間（毫秒級）',
    `updated_at`            DATETIME(3) NULL COMMENT '更新時間（毫秒級）',
    INDEX                   idx_stocks_item_id (`item_id`),
    INDEX                   idx_stocks_warehouse_id (`warehouse_id`),
    INDEX                   idx_stocks_storage_location_id (`storage_location_id`)
) COMMENT = '商品庫存狀態表';

-- 創建 stock_movements 表 - 商品庫存異動歷史表（已包含 1.1.3 版本的修改，並移除了 1.1.4 版本刪除的 slip_id）
CREATE TABLE IF NOT EXISTS stock_movements
(
    `id`                    BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '庫存異動記錄唯一識別碼，自增',
    `item_id`               BIGINT NULL COMMENT '商品ID',
    `warehouse_id`          BIGINT NULL COMMENT '倉庫ID',
    `storage_location_id`   BIGINT NULL COMMENT '儲位ID',
    `type`                  INT NULL COMMENT '異動類型（數字代碼，如入庫=1，出庫=2）',
    `quantity_change`       DECIMAL(18,6) NULL COMMENT '異動數量',
    `note`                  TEXT NULL COMMENT '異動備註',
    `created_at`            DATETIME(3) NULL COMMENT '異動發生時間（毫秒級）',
    `updated_at`            DATETIME(3) NULL COMMENT '更新時間（毫秒級）',
    INDEX                   idx_stock_movements_item_created (`item_id`, `created_at`),
    INDEX                   idx_stock_movements_created_at (`created_at`),
    INDEX                   idx_stock_movements_warehouse_id (`warehouse_id`),
    INDEX                   idx_stock_movements_storage_location_id (`storage_location_id`)
) COMMENT = '商品庫存異動歷史表';

-- ========================================
-- 第三部分：人員、倉庫與單據相關表 (版本 1.1.3)
-- ========================================

-- 創建 users 表 - 人員表
CREATE TABLE IF NOT EXISTS users
(
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '人員唯一識別碼，自增',
    `name`       VARCHAR(200) NULL COMMENT '姓名',
    `role`       VARCHAR(200) NULL COMMENT '角色（倉管、管理員等）',
    `status`     TINYINT NULL COMMENT '狀態：0=停用, 1=啟用, 2=停職',
    `created_at` DATETIME(3) NULL COMMENT '建立時間（毫秒級）',
    `updated_at` DATETIME(3) NULL COMMENT '更新時間（毫秒級）',
    INDEX        idx_users_status (`status`),
    INDEX        idx_users_role (`role`)
) COMMENT = '人員表';

-- 創建 warehouses 表 - 倉庫表
CREATE TABLE IF NOT EXISTS warehouses
(
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '倉庫唯一識別碼，自增',
    `name`       VARCHAR(200) NULL COMMENT '倉庫名稱',
    `type`       VARCHAR(200) NULL COMMENT '類型（常溫/冷藏/GMP等）',
    `location`   VARCHAR(200) NULL COMMENT '地點/地址',
    `area_m2`    DECIMAL(10,2) NULL COMMENT '總面積（單位：平方公尺）',
    `status`     TINYINT NULL COMMENT '狀態：0=停用, 1=啟用',
    `created_at` DATETIME(3) NULL COMMENT '建立時間（毫秒級）',
    `updated_at` DATETIME(3) NULL COMMENT '更新時間（毫秒級）',
    INDEX        idx_warehouses_status (`status`),
    INDEX        idx_warehouses_type (`type`)
) COMMENT = '倉庫表';

-- 創建 storage_locations 表 - 儲位表
CREATE TABLE IF NOT EXISTS storage_locations
(
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '儲位唯一識別碼，自增',
    `warehouse_id` BIGINT NULL COMMENT '所屬倉庫ID',
    `code`         VARCHAR(200) NULL COMMENT '儲位編號（如 A01-B02）',
    `zone`         VARCHAR(200) NULL COMMENT '區域/分區',
    `capacity`     INTEGER NULL COMMENT '可容納容量（數量或件數）',
    `unit`         VARCHAR(200) NULL COMMENT '單位（箱、件、kg）',
    `size_limit`   VARCHAR(200) NULL COMMENT '尺寸限制（[長,寬,高]，單位 mm）',
    `weight_limit` DECIMAL(10,2) NULL COMMENT '承重限制（單位：kg）',
    `status`       TINYINT NULL COMMENT '狀態：0=停用, 1=啟用, 2=維護中',
    `created_at`   DATETIME(3) NULL COMMENT '建立時間（毫秒級）',
    `updated_at`   DATETIME(3) NULL COMMENT '更新時間（毫秒級）',
    INDEX          idx_storage_locations_warehouse_id (`warehouse_id`),
    INDEX          idx_storage_locations_code (`code`),
    INDEX          idx_storage_locations_status (`status`)
) COMMENT = '儲位表';

-- 創建 slips 表 - 單據表
CREATE TABLE IF NOT EXISTS slips
(
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '單據唯一識別碼，自增',
    `slips_type` TINYINT NULL COMMENT '單據類型：1=入庫單, 2=出庫單, 3=轉倉單, 4=報廢單',
    `created_by` BIGINT NULL COMMENT '建立人（users.id）',
    `status`     TINYINT NULL COMMENT '狀態：0=草稿, 1=完成, 2=取消',
    `created_at` DATETIME(3) NULL COMMENT '建立時間（毫秒級）',
    `updated_at` DATETIME(3) NULL COMMENT '更新時間（毫秒級）',
    INDEX        idx_slips_slips_type (`slips_type`),
    INDEX        idx_slips_created_by (`created_by`),
    INDEX        idx_slips_status (`status`),
    INDEX        idx_slips_created_at (`created_at`)
) COMMENT = '單據表';

-- ========================================
-- 第四部分：單據與庫存關聯表 (版本 1.1.4)
-- ========================================

-- 創建 slip_movements 表 - 單據與庫存異動關聯表
CREATE TABLE IF NOT EXISTS slip_movements
(
    `id`                 BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '關聯記錄唯一識別碼，自增',
    `slip_id`            BIGINT NOT NULL COMMENT '單據ID（關聯到 slips.id）',
    `stock_movement_id`  BIGINT NOT NULL COMMENT '庫存異動ID（關聯到 stock_movements.id）',
    `created_at`         DATETIME(3) NULL COMMENT '建立時間（毫秒級）',
    `updated_at`         DATETIME(3) NULL COMMENT '更新時間（毫秒級）',
    INDEX                idx_slip_movements_slip_id (`slip_id`),
    INDEX                idx_slip_movements_stock_movement_id (`stock_movement_id`),
    UNIQUE INDEX         uk_slip_movements_slip_stock (`slip_id`, `stock_movement_id`)
) COMMENT = '單據與庫存異動關聯表';

-- 創建 slip_details 表 - 單據明細表
CREATE TABLE IF NOT EXISTS slip_details
(
    `id`                        BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明細唯一識別碼，自增',
    `line_number`               INTEGER NOT NULL COMMENT '項次（在單據中的順序）',
    `slip_id`                   BIGINT NOT NULL COMMENT '單據ID（關聯到 slips.id）',
    `item_id`                   BIGINT NOT NULL COMMENT '商品ID（關聯到 items.id）',
    `from_warehouse_id`         BIGINT NULL COMMENT '來源倉庫ID（關聯到 warehouses.id）',
    `from_storage_location_id`  BIGINT NULL COMMENT '來源儲位ID（關聯到 storage_locations.id）',
    `to_warehouse_id`           BIGINT NULL COMMENT '目標倉庫ID（關聯到 warehouses.id）',
    `to_storage_location_id`    BIGINT NULL COMMENT '目標儲位ID（關聯到 storage_locations.id）',
    `quantity_change`           DECIMAL(18,6) NOT NULL COMMENT '異動數量',
    `status`                    TINYINT NULL COMMENT '狀態：0=待處理, 1=已處理, 2=取消',
    `note`                      TEXT NULL COMMENT '異動備註',
    `created_at`                DATETIME(3) NULL COMMENT '建立時間（毫秒級）',
    `updated_at`                DATETIME(3) NULL COMMENT '更新時間（毫秒級）',
    INDEX                       idx_slip_details_slip_id (`slip_id`),
    INDEX                       idx_slip_details_slip_status (`slip_id`, `status`),
    INDEX                       idx_slip_details_item_id (`item_id`),
    INDEX                       idx_slip_details_from_warehouse_id (`from_warehouse_id`),
    INDEX                       idx_slip_details_from_storage_location_id (`from_storage_location_id`),
    INDEX                       idx_slip_details_to_warehouse_id (`to_warehouse_id`),
    INDEX                       idx_slip_details_to_storage_location_id (`to_storage_location_id`)
) COMMENT = '單據明細表';

-- ========================================
-- 第五部分：初始化配置數據
-- ========================================

-- 插入資料庫版本記錄
INSERT INTO key_values (`key`, `value`, `description`)
VALUES ('DBversion', '1.1.4', '資料庫架構版本');

-- 提交事務
COMMIT;

-- 顯示初始化完成訊息
SELECT '資料庫架構成功初始化至版本 1.1.4' AS 結果;
