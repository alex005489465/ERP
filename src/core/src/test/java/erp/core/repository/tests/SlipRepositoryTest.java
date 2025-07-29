package erp.core.repository.tests;

import erp.core.entity.Slip;
import erp.core.repository.SlipRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class SlipRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SlipRepository slipRepository;

    // 用於手動依賴注入的setter方法
    public void setEntityManager(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setSlipRepository(SlipRepository slipRepository) {
        this.slipRepository = slipRepository;
    }

    @Test
    public void testSlipRepository() {
        // 創建測試數據
        Slip slip = new Slip();
        slip.setSlipsType((byte) 1); // 入庫單
        slip.setCreatedBy(1L);
        slip.setStatus((byte) 0); // 草稿
        
        // 保存並刷新
        Slip savedSlip = slipRepository.save(slip);
        entityManager.flush();
        
        // 測試基本查詢
        Optional<Slip> foundSlip = slipRepository.findById(savedSlip.getId());
        assertThat(foundSlip).isPresent();
        assertThat(foundSlip.get().getSlipsType()).isEqualTo((byte) 1);
        
        // 測試按單據類型查找
        List<Slip> slipsByType = slipRepository.findBySlipsType((byte) 1);
        assertThat(slipsByType).hasSize(1);
        
        // 測試按建立人查找
        List<Slip> slipsByCreatedBy = slipRepository.findByCreatedBy(1L);
        assertThat(slipsByCreatedBy).hasSize(1);
        
        // 測試按狀態查找
        List<Slip> slipsByStatus = slipRepository.findByStatus((byte) 0);
        assertThat(slipsByStatus).hasSize(1);
        
        // 測試統計功能
        Long count = slipRepository.countBySlipsTypeAndStatus((byte) 1, (byte) 0);
        assertThat(count).isEqualTo(1L);
    }
}