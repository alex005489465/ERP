package erp.core.repository.tests;

import erp.core.entity.User;
import erp.core.repository.UserRepository;
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
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    // 用於手動依賴注入的setter方法
    public void setEntityManager(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    public void testUserRepository() {
        // 創建測試數據
        User user = new User();
        user.setName("測試使用者");
        user.setRole("倉管");
        user.setStatus((byte) 1); // 啟用
        
        // 保存並刷新
        User savedUser = userRepository.save(user);
        entityManager.flush();
        
        // 測試基本查詢
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("測試使用者");
        
        // 測試按姓名查找
        Optional<User> userByName = userRepository.findByName("測試使用者");
        assertThat(userByName).isPresent();
        
        // 測試按角色查找
        List<User> usersByRole = userRepository.findByRole("倉管");
        assertThat(usersByRole).hasSize(1);
        
        // 測試按狀態查找
        List<User> activeUsers = userRepository.findByStatus((byte) 1);
        assertThat(activeUsers).hasSize(1);
        
        // 測試統計功能
        Long activeUserCount = userRepository.countActiveUsers();
        assertThat(activeUserCount).isEqualTo(1L);
    }
}