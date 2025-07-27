package core.application.usecase;

import core.contract.usecase.ICreateStockMovementUseCase;
import core.contract.usecase.dto.CreateStockMovementInput;
import core.contract.usecase.dto.CreateStockMovementOutput;
import core.library.base.BaseUseCase;
import core.library.base.BaseRepository;
import core.domain.entity.Stock;
import core.domain.entity.StockMovement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 創建庫存異動用例實作
 * 負責處理庫存異動記錄的創建業務邏輯
 */
@Service
public class CreateStockMovementUseCase extends BaseUseCase<CreateStockMovementInput, CreateStockMovementOutput> 
        implements ICreateStockMovementUseCase {

    private final BaseRepository<Stock, Long> stockRepository;
    private final BaseRepository<StockMovement, Long> stockMovementRepository;

    @Autowired
    public CreateStockMovementUseCase(
            BaseRepository<Stock, Long> stockRepository,
            BaseRepository<StockMovement, Long> stockMovementRepository) {
        this.stockRepository = stockRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

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
    public CreateStockMovementOutput maintask(CreateStockMovementInput input) {
        try {
            // 1. 驗證輸入參數
            if (!validateInput(input)) {
                return createFailureResult("輸入參數驗證失敗");
            }

            // 2. 查詢當前庫存
            BigDecimal currentQuantity = getCurrentStockQuantity(
                input.getItemId(),
                input.getLocation()
            );

            // 3. 計算新庫存數量
            BigDecimal newQuantity = calculateNewQuantity(currentQuantity, input.getQuantityChange());

            // 4. 驗證業務規則
            if (!validateBusinessRules(input, currentQuantity, newQuantity)) {
                return createFailureResult("業務規則驗證失敗");
            }

            // 5. 更新庫存數量
            if (!updateStockQuantity(input.getItemId(), input.getLocation(), newQuantity)) {
                return createFailureResult("更新庫存數量失敗");
            }

            // 6. 創建異動記錄
            if (!createStockMovementRecord(input)) {
                return createFailureResult("創建異動記錄失敗");
            }

            // 7. 返回成功結果
            return createSuccessResult(input);

        } catch (Exception e) {
            return createFailureResult("系統錯誤：" + e.getMessage());
        }
    }

    /**
     * 驗證輸入參數的有效性
     * 檢查必要欄位是否完整且符合格式要求
     * 
     * @param input 輸入參數
     * @return 驗證結果，true 表示有效，false 表示無效
     */
    @Override
    public boolean validateInput(CreateStockMovementInput input) {
        if (input == null) {
            return false;
        }

        // 檢查商品ID
        if (input.getItemId() == null || input.getItemId() <= 0) {
            return false;
        }

        // 檢查存放位置
        if (input.getLocation() == null || input.getLocation().trim().isEmpty()) {
            return false;
        }

        // 檢查異動類型
        if (input.getType() == null) {
            return false;
        }

        // 檢查數量變化
        if (input.getQuantityChange() == null) {
            return false;
        }

        return true;
    }

    /**
     * 查詢指定商品和位置的當前庫存數量
     * 
     * @param itemId 商品ID
     * @param location 存放位置
     * @return 當前庫存數量，如果不存在則返回 0
     */
    @Override
    public BigDecimal getCurrentStockQuantity(Long itemId, String location) {
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("itemId", itemId);
            conditions.put("location", location);
            
            Optional<Stock> stockOptional = stockRepository.findFirst(conditions);
            
            if (stockOptional.isPresent()) {
                BigDecimal quantity = stockOptional.get().getQuantity();
                return quantity != null ? quantity : BigDecimal.ZERO;
            }
            
            return BigDecimal.ZERO;
        } catch (Exception e) {
            // 查詢失敗時返回 0
            return BigDecimal.ZERO;
        }
    }

    /**
     * 計算異動後的新庫存數量
     * 
     * @param currentQuantity 當前庫存數量
     * @param quantityChange 數量變化
     * @return 計算後的新庫存數量
     */
    @Override
    public BigDecimal calculateNewQuantity(BigDecimal currentQuantity, BigDecimal quantityChange) {
        if (currentQuantity == null) {
            currentQuantity = BigDecimal.ZERO;
        }
        if (quantityChange == null) {
            quantityChange = BigDecimal.ZERO;
        }
        return currentQuantity.add(quantityChange);
    }

    /**
     * 驗證庫存異動的業務規則
     * 例如：檢查出庫時庫存是否足夠、數量是否為負數等
     * 
     * @param input 輸入參數
     * @param currentQuantity 當前庫存數量
     * @param newQuantity 異動後的新數量
     * @return 驗證結果，true 表示通過，false 表示不通過
     */
    @Override
    public boolean validateBusinessRules(CreateStockMovementInput input, BigDecimal currentQuantity, BigDecimal newQuantity) {
        // 檢查新庫存數量不能為負數
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }

        // 額外的業務規則檢查可以在此處添加
        // 例如：檢查特定商品的最大庫存限制等

        return true;
    }

    /**
     * 更新庫存數量
     * 如果庫存記錄不存在則創建新記錄，存在則更新數量
     * 
     * @param itemId 商品ID
     * @param location 存放位置
     * @param newQuantity 新的庫存數量
     * @return 更新結果，true 表示成功，false 表示失敗
     */
    @Override
    public boolean updateStockQuantity(Long itemId, String location, BigDecimal newQuantity) {
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("itemId", itemId);
            conditions.put("location", location);
            
            Optional<Stock> stockOptional = stockRepository.findFirst(conditions);
            
            Stock stock;
            if (stockOptional.isPresent()) {
                // 更新現有庫存記錄
                stock = stockOptional.get();
                stock.setQuantity(newQuantity);
            } else {
                // 創建新的庫存記錄
                stock = new Stock(itemId, location, newQuantity);
            }
            
            stockRepository.save(stock);
            return true;
            
        } catch (Exception e) {
            // 更新失敗
            return false;
        }
    }

    /**
     * 創建庫存異動記錄
     * 將異動操作記錄保存到 stock_movements 表中
     * 
     * @param input 輸入參數
     * @return 創建結果，true 表示成功，false 表示失敗
     */
    @Override
    public boolean createStockMovementRecord(CreateStockMovementInput input) {
        try {
            StockMovement stockMovement = new StockMovement(
                input.getItemId(),
                input.getLocation(),
                input.getType(),
                input.getQuantityChange(),
                input.getNote()
            );
            
            stockMovementRepository.save(stockMovement);
            return true;
            
        } catch (Exception e) {
            // 創建異動記錄失敗
            return false;
        }
    }

    /**
     * 生成操作成功的結果訊息
     * 
     * @param input 輸入參數
     * @return 成功結果輸出
     */
    @Override
    public CreateStockMovementOutput createSuccessResult(CreateStockMovementInput input) {
        return new CreateStockMovementOutput(true, "庫存異動記錄創建成功");
    }

    /**
     * 生成操作失敗的結果訊息
     * 
     * @param errorMessage 錯誤訊息
     * @return 失敗結果輸出
     */
    @Override
    public CreateStockMovementOutput createFailureResult(String errorMessage) {
        return new CreateStockMovementOutput(errorMessage);
    }
}