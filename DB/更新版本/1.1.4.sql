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

        -- 如果 DBversion = 1.1.3，則繼續執行
        IF
        (SELECT `value` FROM key_values WHERE `key` = 'DBversion') = '1.1.3' THEN
            START TRANSACTION;

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

            -- 將現有的 slip_id 數據遷移到新的關聯表中
            INSERT INTO slip_movements (`slip_id`, `stock_movement_id`, `created_at`, `updated_at`)
            SELECT 
                `slip_id`,
                `id` as stock_movement_id,
                NOW(3) as created_at,
                NOW(3) as updated_at
            FROM stock_movements 
            WHERE `slip_id` IS NOT NULL;

            -- 從 stock_movements 表中刪除 slip_id 欄位
            ALTER TABLE stock_movements 
            DROP INDEX idx_stock_movements_slip_id;

            ALTER TABLE stock_movements 
            DROP COLUMN `slip_id`;

            -- 更新 DBversion 記錄
            UPDATE key_values
            SET `value` = '1.1.4'
            WHERE `key` = 'DBversion';

            COMMIT;

            SELECT '資料庫架構成功更新至版本 1.1.4' AS 結果;
            ELSE
            SELECT '資料庫架構版本不符合要求，跳過更新' AS 結果;
        END IF;

END$$

DELIMITER ;

-- 3. 執行存儲過程
CALL updateDatabase();

-- 4. 刪除存儲過程
DROP PROCEDURE IF EXISTS updateDatabase;
