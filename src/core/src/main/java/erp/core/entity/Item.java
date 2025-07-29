package erp.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品唯一識別碼，自增'")
    private Long id;
    
    @Column(length = 100, nullable = true, columnDefinition = "VARCHAR(100) NULL COMMENT '商品名稱'")
    private String name;
    
    @Column(length = 20, nullable = true, columnDefinition = "VARCHAR(20) NULL COMMENT '單位（例如個、箱）'")
    private String unit;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true, columnDefinition = "DATETIME(3) NULL COMMENT '建立時間（毫秒級）'")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true, columnDefinition = "DATETIME(3) NULL COMMENT '更新時間（毫秒級）'")
    private LocalDateTime updatedAt;
}