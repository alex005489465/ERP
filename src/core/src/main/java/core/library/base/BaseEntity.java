
package core.library.base;

import core.contract.entity.IEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 基礎實體抽象類別
 * 統一 ID、時間欄位等標準欄位與行為
 */
@MappedSuperclass
public abstract class BaseEntity implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "created_at", columnDefinition = "DATETIME(3)")
    protected LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(3)")
    protected LocalDateTime updatedAt;

    /**
     * 在實體被保存之前自動設定時間
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * 在實體被更新之前自動設定更新時間
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}