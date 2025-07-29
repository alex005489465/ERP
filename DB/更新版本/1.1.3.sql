-- 使用 erp_db 數據庫
USE erp_db;

-- 1. 檢查並刪除已存在的存儲過程
DROP PROCEDURE IF EXISTS updateDatabase;

-- 2. 定義存儲過程
DELIMITER $$

CREATE PROCEDURE updateDatabase()
    BEGIN
        DECLARE canUpdate INT DEFAULT 0;
        DECLARE EXIT HANDLER FOR SQLEXCEPTION
            BEGIN
                ROLLBACK;
                RESIGNAL;
            END;

        -- 如果 DBversion = 1.1.2，則繼續執行
        IF
        (SELECT `value` FROM key_values WHERE `key` = 'DBversion') = '1.1.2' THEN
            START TRANSACTION;

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

            -- 修改 stocks 表 - 商品庫存狀態表
            -- 添加新欄位
            ALTER TABLE stocks 
            ADD COLUMN `warehouse_id` BIGINT NULL COMMENT '倉庫ID' AFTER `item_id`,
            ADD COLUMN `storage_location_id` BIGINT NULL COMMENT '儲位ID' AFTER `warehouse_id`;

            -- 移除舊欄位
            ALTER TABLE stocks 
            DROP COLUMN `location`;

            -- 添加新索引
            ALTER TABLE stocks 
            ADD INDEX idx_stocks_warehouse_id (`warehouse_id`),
            ADD INDEX idx_stocks_storage_location_id (`storage_location_id`);

            -- 修改 stock_movements 表 - 商品庫存異動歷史表
            -- 添加新欄位
            ALTER TABLE stock_movements 
            ADD COLUMN `warehouse_id` BIGINT NULL COMMENT '倉庫ID' AFTER `item_id`,
            ADD COLUMN `storage_location_id` BIGINT NULL COMMENT '儲位ID' AFTER `warehouse_id`,
            ADD COLUMN `slip_id` BIGINT NULL COMMENT '單據ID' AFTER `quantity_change`;

            -- 移除舊欄位
            ALTER TABLE stock_movements 
            DROP COLUMN `location`;

            -- 添加新索引
            ALTER TABLE stock_movements 
            ADD INDEX idx_stock_movements_warehouse_id (`warehouse_id`),
            ADD INDEX idx_stock_movements_storage_location_id (`storage_location_id`),
            ADD INDEX idx_stock_movements_slip_id (`slip_id`);

            -- 更新 DBversion 記錄
            UPDATE key_values
            SET `value` = '1.1.3'
            WHERE `key` = 'DBversion';

            COMMIT;

            SELECT '資料庫架構成功更新至版本 1.1.3' AS 結果;
            ELSE
            SELECT '資料庫架構版本不符合要求，跳過更新' AS 結果;
        END IF;

END$$

DELIMITER ;

-- 3. 執行存儲過程
CALL updateDatabase();

-- 4. 刪除存儲過程
DROP PROCEDURE IF EXISTS updateDatabase;
