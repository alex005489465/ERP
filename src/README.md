此資料夾是 ERP 系統的核心原始碼所在。主要包含兩個子模組：

## core
核心後端應用程式，採用 Java 21 和 Spring Boot 3.5.4 框架開發。包含主要業務邏輯、RESTful API 端點、資料存取層以及相關的單元測試，是整個 ERP 系統的核心。

## e2e-api-test  
端對端 API 自動化測試模組，使用 TypeScript 和 Jest 框架編寫。針對後端 API 進行完整的功能測試，確保系統各項功能的正確性與穩定性。
