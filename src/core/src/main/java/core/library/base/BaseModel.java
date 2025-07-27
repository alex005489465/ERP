package core.library.base;

import core.contract.model.IModel;

/**
 * 基礎模型抽象類別
 * 定義純領域模型（非資料庫映射實體）的基本行為
 * 用於 DDD 中的 Value Object、Aggregate Root 等模型
 */
public abstract class BaseModel implements IModel {
    
    @Override
    public boolean sameIdentityAs(IModel other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        return this.equals(other);
    }
    
    /**
     * 子類別應該覆寫此方法以提供具體的相等性比較邏輯
     * @param obj 要比較的物件
     * @return 是否相等
     */
    @Override
    public abstract boolean equals(Object obj);
    
    /**
     * 子類別應該覆寫此方法以提供一致的雜湊碼
     * @return 雜湊碼
     */
    @Override
    public abstract int hashCode();
}