package core.configuration;

import core.domain.entity.Stock;
import core.domain.entity.StockMovement;
import core.library.base.BaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;

/**
 * Repository 配置類別
 * 用於配置和註冊各種 Repository Bean
 */
@Configuration
public class RepositoryConfiguration {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 創建 Stock 實體的 Repository Bean
     */
    @Bean
    public BaseRepository<Stock, Long> stockRepository() {
        @SuppressWarnings("unchecked")
        JpaEntityInformation<Stock, Long> entityInformation =
            (JpaEntityInformation<Stock, Long>) JpaEntityInformationSupport.getEntityInformation(Stock.class, entityManager);
        return new BaseRepository<>(entityInformation, entityManager);
    }

    /**
     * 創建 StockMovement 實體的 Repository Bean
     */
    @Bean
    public BaseRepository<StockMovement, Long> stockMovementRepository() {
        @SuppressWarnings("unchecked")
        JpaEntityInformation<StockMovement, Long> entityInformation =
            (JpaEntityInformation<StockMovement, Long>) JpaEntityInformationSupport.getEntityInformation(StockMovement.class, entityManager);
        return new BaseRepository<>(entityInformation, entityManager);
    }
}
