package core.library.contract.usecase;

/**
 * 所有應用層 Use Case 的統一執行入口
 * 統一 execute(Input input): Output 標準執行模式
 */
public interface IUseCase<Input, Output> {
    
    /**
     * 執行業務邏輯
     * @param input 輸入參數
     * @return 處理結果（可為同步或異步）
     */
    Output execute(Input input);
}