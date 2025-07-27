package core.library.base;

import core.interface.usecase.IUseCase;

/**
 * 基礎用例抽象類別
 * 統一 execute(Input input): Output 標準執行模式
 * 所有應用層 Use Case 的統一執行入口
 */
public abstract class BaseUseCase<Input, Output> implements IUseCase<Input, Output> {
    
    /**
     * 執行業務邏輯
     * 統一執行入口，子類別不可覆蓋此方法
     * @param input 輸入參數
     * @return 處理結果（可為同步或異步）
     */
    @Override
    public final Output execute(Input input) {
        return maintask(input);
    }
    
    /**
     * 主要任務邏輯
     * 子類別必須實作此方法以提供具體的業務邏輯處理
     * @param input 輸入參數
     * @return 處理結果（可為同步或異步）
     */
    @Override
    public abstract Output maintask(Input input);
}