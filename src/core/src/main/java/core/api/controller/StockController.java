package core.api.controller;

import core.contract.controller.dto.ApiResponse;
import core.contract.usecase.ICreateStockMovementUseCase;
import core.contract.usecase.dto.CreateStockMovementInput;
import core.contract.usecase.dto.CreateStockMovementOutput;
import core.library.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 庫存控制器
 * 負責處理庫存相關的 HTTP 請求
 */
@RestController
@RequestMapping("/api/stock")
public class StockController extends BaseController {

    private final ICreateStockMovementUseCase createStockMovementUseCase;

    @Autowired
    public StockController(ICreateStockMovementUseCase createStockMovementUseCase) {
        this.createStockMovementUseCase = createStockMovementUseCase;
    }

    /**
     * 變更庫存 - POST 請求
     * 
     * @param input 庫存異動輸入參數
     * @return 庫存異動結果
     */
    @PostMapping("/movement")
    public ResponseEntity<ApiResponse<CreateStockMovementOutput>> movement(@RequestBody CreateStockMovementInput input) {
        try {
            // 執行庫存異動用例
            CreateStockMovementOutput output = createStockMovementUseCase.execute(input);
            
            // 根據用例執行結果返回相應的回應
            if (output.isSuccess()) {
                return success(output);
            } else {
                return (ResponseEntity<ApiResponse<CreateStockMovementOutput>>) (ResponseEntity<?>) error(output.getMessage());
            }
        } catch (Exception e) {
            // 處理異常情況
            return (ResponseEntity<ApiResponse<CreateStockMovementOutput>>) (ResponseEntity<?>) error("庫存異動處理失敗: " + e.getMessage());
        }
    }
}