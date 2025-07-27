package core.library.base;

import core.interface.entity.IEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 基礎實體抽象類別
 * 統一 ID、時間欄位等標準欄位與行為
 */
public abstract class BaseEntity implements IEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    
    @Column(columnDefinition = "DATETIME(3)")
    protected LocalDateTime createdAt;
    
    @Column(columnDefinition = "DATETIME(3)")
    protected LocalDateTime updatedAt;
    
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