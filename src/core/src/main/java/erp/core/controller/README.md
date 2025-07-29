# ERP 系統控制器設計原則

### 1. 統一的註解配置

所有控制器都應使用以下標準註解：

**說明：**
- `@RestController`: 標識為 REST API 控制器
- `@RequestMapping`: 定義基礎路徑，遵循 `/api/{模組名稱}` 格式
- `@RequiredArgsConstructor`: 自動生成依賴注入構造函數
- `@Slf4j`: 提供日誌記錄功能

### 2. 統一的響應格式

所有 API 端點都必須返回 `ApiResponse<T>` 類型。

**響應格式包含：**
- 成功響應：`ApiResponse.success(message, data)`
- 錯誤響應：`ApiResponse.error(message, errorCode)`

### 3. 標準化的錯誤處理

每個控制器方法都應實現完整的異常處理機制。

**錯誤處理層級：**
1. `IllegalArgumentException`: 參數驗證錯誤
2. `IllegalStateException`: 業務狀態錯誤
3. `Exception`: 系統未預期錯誤

### 4. 參數處理標準

#### 4.1 請求參數格式
- 統一使用 `Map<String, Object>` 接收請求參數
- 支援靈活的參數結構和動態解析

#### 4.2 參數驗證模式
參數驗證應包含數值型參數處理、字串型參數處理和參數必填驗證等標準流程。

### 5. 日誌記錄規範

#### 5.1 日誌級別使用
- `log.info()`: 記錄正常業務操作
- `log.warn()`: 記錄參數錯誤和業務異常
- `log.error()`: 記錄系統錯誤和未預期異常

#### 5.2 日誌內容格式
日誌內容應包含請求日誌、警告日誌和錯誤日誌等不同級別的記錄格式。

### 6. 依賴注入模式

使用 `final` 關鍵字聲明服務依賴，配合 `@RequiredArgsConstructor` 註解實現依賴注入。

### 7. API 端點設計規範

#### 7.1 路徑命名
- 基礎路徑：`/api/{模組}`
- 操作路徑：使用動詞或名詞描述功能
- 禁止事項: 不能使用浮動名稱，比如 `/api/{id}/operation` 或 `/api/item/{id}`，這種東西應該放在請求體內。
- 範例：
  - `/api/slips` - 單據管理
  - `/api/warehouse/stock` - 庫存查詢
  - `/api/warehouse/movement` - 異動記錄

#### 7.2 HTTP 方法使用
- 只能使用 `@PostMapping` 進行資料操作
- 支援複雜的請求參數結構
- 統一的請求體格式

### 8. 文檔化要求

每個控制器和方法都應包含完整的中文註釋，包括控制器功能描述、端點說明、方法功能描述和支援的操作模式等。
