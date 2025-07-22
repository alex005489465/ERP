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

        -- 檢查是否存在 key_values 表和 DBversion 記錄
        SELECT COUNT(*)
        INTO canUpdate
        FROM INFORMATION_SCHEMA.TABLES
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'key_values';

        IF
        canUpdate > 0 THEN
        SELECT COUNT(*)
        INTO canUpdate
        FROM key_values
        WHERE `key` = 'DBversion';
        END IF;

        -- 如果 DBversion 不存在，則繼續執行
        IF
        canUpdate = 0 THEN
            START TRANSACTION;

            -- 刪除表（如果存在）
            DROP TABLE IF EXISTS key_values;

            -- 創建新的 key_values 表
            CREATE TABLE key_values
            (
                `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
                `key`         VARCHAR(255) NOT NULL,
                `value`       TEXT NULL,
                `description` TEXT NULL,
                `created_at`  DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3),
                `updated_at`  DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
                INDEX         idx_key (`key`)
            );

            -- 插入 DBversion 記錄
            INSERT INTO key_values (`key`, `value`, `description`)
            VALUES ('DBversion', '1.1.1', '資料庫架構版本');

            COMMIT;

            SELECT '資料庫架構成功更新至版本 1.1.1' AS 結果;
            ELSE
            SELECT '資料庫架構已存在，跳過更新' AS 結果;
        END IF;

END$$

DELIMITER ;

-- 3. 執行存儲過程
CALL updateDatabase();

-- 4. 刪除存儲過程
DROP PROCEDURE IF EXISTS updateDatabase;