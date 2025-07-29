package erp.core.repository.tests;

import erp.core.entity.Item;
import erp.core.repository.ItemRepository;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Setter
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testItemRepository() {
        // 創建測試數據
        Item item = new Item();
        item.setName("測試商品");
        item.setUnit("個");
        
        // 保存並刷新
        Item savedItem = itemRepository.save(item);
        entityManager.flush();
        
        // 測試基本查詢
        Optional<Item> foundItem = itemRepository.findById(savedItem.getId());
        assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getName()).isEqualTo("測試商品");
        
        // 測試按名稱查找
        Optional<Item> itemByName = itemRepository.findByName("測試商品");
        assertThat(itemByName).isPresent();
        
        // 測試存在性檢查
        boolean exists = itemRepository.existsByName("測試商品");
        assertThat(exists).isTrue();
        
        // 測試模糊查詢
        List<Item> itemsContaining = itemRepository.findByNameContainingIgnoreCase("測試");
        assertThat(itemsContaining).hasSize(1);
    }
}