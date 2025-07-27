package core.contract.model;

/**
 * 定義純領域模型（非資料庫映射實體）的基本行為
 * 用於 DDD 中的 Value Object、Aggregate Root 等模型
 * 未與 ORM 直接耦合的邏輯屬性
 */
public interface IModel {
    
    /**
     * 判斷是否為相同業務邏輯實體
     * @param other 要比較的模型物件
     * @return 如果為相同業務邏輯實體則返回 true，否則返回 false
     */
    boolean sameIdentityAs(IModel other);
}