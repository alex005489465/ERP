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

        -- 如果 DBversion = 1.1.1，則繼續執行
        IF
        (SELECT `value` FROM key_values WHERE `key` = 'DBversion') = '1.1.1' THEN
            START TRANSACTION;

            -- 創建 items 表 - 商品基本資料表
            CREATE TABLE IF NOT EXISTS items
            (
                `id`         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品唯一識別碼，自增',
                `name`       VARCHAR(100) NULL COMMENT '商品名稱',
                `unit`       VARCHAR(20) NULL COMMENT '單位（例如個、箱）',
                `created_at` DATETIME(3) NULL COMMENT '建立時間（毫秒級）',
                `updated_at` DATETIME(3) NULL COMMENT '更新時間（毫秒級）'
            ) COMMENT = '商品基本資料表';

            -- 創建 stocks 表 - 商品庫存狀態表
            CREATE TABLE IF NOT EXISTS stocks
            (
                `id`         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '庫存記錄唯一識別碼，自增',
                `item_id`    BIGINT NULL COMMENT '對應商品ID',
                `location`   VARCHAR(50) NULL COMMENT '庫存地點名稱（自由文字）',
                `quantity`   DECIMAL(18,6) NULL COMMENT '現有庫存量',
                `created_at` DATETIME(3) NULL COMMENT '建立時間（毫秒級）',
                `updated_at` DATETIME(3) NULL COMMENT '更新時間（毫秒級）',
                INDEX        idx_item_id (`item_id`),
                INDEX        idx_location (`location`)
            ) COMMENT = '商品庫存狀態表';

            -- 創建 stock_movements 表 - 商品庫存異動歷史表
            CREATE TABLE IF NOT EXISTS stock_movements
            (
                `id`             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '庫存異動記錄唯一識別碼，自增',
                `item_id`        BIGINT NULL COMMENT '商品ID',
                `location`       VARCHAR(50) NULL COMMENT '庫存地點名稱（自由文字）',
                `type`           INT NULL COMMENT '異動類型（數字代碼，如入庫=1，出庫=2）',
                `quantity_change` DECIMAL(18,6) NULL COMMENT '異動數量',
                `note`           TEXT NULL COMMENT '異動備註',
                `created_at`     DATETIME(3) NULL COMMENT '異動發生時間（毫秒級）',
                INDEX            idx_item_location_time (`item_id`, `created_at`),
                INDEX            idx_created_at (`created_at`)
            ) COMMENT = '商品庫存異動歷史表';

            -- 更新 DBversion 記錄
            UPDATE key_values
            SET `value` = '1.1.2'
            WHERE `key` = 'DBversion';

            COMMIT;

            SELECT '資料庫架構成功更新至版本 1.1.2' AS 結果;
            ELSE
            SELECT '資料庫架構版本不符合要求，跳過更新' AS 結果;
        END IF;

END$$

DELIMITER ;

-- 3. 執行存儲過程
CALL updateDatabase();

-- 4. 刪除存儲過程
DROP PROCEDURE IF EXISTS updateDatabase;