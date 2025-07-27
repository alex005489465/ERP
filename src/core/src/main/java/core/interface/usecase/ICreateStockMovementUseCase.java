package core.interface.usecase;

import core.interface.usecase.dto.CreateStockMovementInput;
import core.interface.usecase.dto.CreateStockMovementOutput;
import java.math.BigDecimal;

/**
 * 創建庫存異動用例接口
 * 負責處理庫存異動記錄的創建業務邏輯
 */
public interface ICreateStockMovementUseCase extends IUseCase<CreateStockMovementInput, CreateStockMovementOutput> {
    
    /**
     * 執行創建庫存異動的主要任務邏輯
     * 主要流程：
     * 1. 驗證輸入參數
     * 2. 查詢當前庫存
     * 3. 驗證業務規則
     * 4. 更新庫存數量
     * 5. 創建異動記錄
     * 
     * @param input 創建庫存異動的輸入參數
     * @return 創建結果
     */
    @Override
    CreateStockMovementOutput maintask(CreateStockMovementInput input);
    
    /**
     * 驗證輸入參數的有效性
     * 檢查必要欄位是否完整且符合格式要求
     * 
     * @param input 輸入參數
     * @return 驗證結果，true 表示有效，false 表示無效
     */
    boolean validateInput(CreateStockMovementInput input);
    
    /**
     * 查詢指定商品和位置的當前庫存數量
     * 
     * @param itemId 商品ID
     * @param location 存放位置
     * @return 當前庫存數量，如果不存在則返回 0
     */
    BigDecimal getCurrentStockQuantity(Long itemId, String location);

    /**
     * 計算異動後的新庫存數量
     * 
     * @param currentQuantity 當前庫存數量
     * @param quantityChange 數量變化
     * @return 計算後的新庫存數量
     */
    BigDecimal calculateNewQuantity(BigDecimal currentQuantity, BigDecimal quantityChange);
    
    /**
     * 驗證庫存異動的業務規則
     * 例如：檢查出庫時庫存是否足夠、數量是否為負數等
     * 
     * @param input 輸入參數
     * @param currentQuantity 當前庫存數量
     * @param newQuantity 異動後的新數量
     * @return 驗證結果，true 表示通過，false 表示不通過
     */
    boolean validateBusinessRules(CreateStockMovementInput input, BigDecimal currentQuantity, BigDecimal newQuantity);
    
    /**
     * 更新庫存數量
     * 如果庫存記錄不存在則創建新記錄，存在則更新數量
     * 
     * @param itemId 商品ID
     * @param location 存放位置
     * @param newQuantity 新的庫存數量
     * @return 更新結果，true 表示成功，false 表示失敗
     */
    boolean updateStockQuantity(Long itemId, String location, BigDecimal newQuantity);

    /**
     * 創建庫存異動記錄
     * 將異動操作記錄保存到 stock_movements 表中
     * 
     * @param input 輸入參數
     * @return 創建結果，true 表示成功，false 表示失敗
     */
    boolean createStockMovementRecord(CreateStockMovementInput input);
    
    /**
     * 生成操作成功的結果訊息
     * 
     * @param input 輸入參數
     * @return 成功結果輸出
     */
    CreateStockMovementOutput createSuccessResult(CreateStockMovementInput input);
    
    /**
     * 生成操作失敗的結果訊息
     * 
     * @param errorMessage 錯誤訊息
     * @return 失敗結果輸出
     */
    CreateStockMovementOutput createFailureResult(String errorMessage);
    
}