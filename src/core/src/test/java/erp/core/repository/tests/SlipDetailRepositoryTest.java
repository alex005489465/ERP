package erp.core.repository.tests;

import erp.core.entity.SlipDetail;
import erp.core.repository.SlipDetailRepository;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Setter
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class SlipDetailRepositoryTest {

    // 用於手動依賴注入的setter方法
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SlipDetailRepository slipDetailRepository;

    @Test
    public void testSlipDetailRepository() {
        // 創建測試數據
        SlipDetail detail1 = new SlipDetail();
        detail1.setLineNumber(1);
        detail1.setSlipId(1L);
        detail1.setItemId(1L);
        detail1.setFromWarehouseId(1L);
        detail1.setFromStorageLocationId(1L);
        detail1.setToWarehouseId(2L);
        detail1.setToStorageLocationId(2L);
        detail1.setQuantityChange(new BigDecimal("10.500"));
        detail1.setStatus(SlipDetail.Status.PENDING);
        detail1.setNote("測試明細1");

        SlipDetail detail2 = new SlipDetail();
        detail2.setLineNumber(2);
        detail2.setSlipId(1L);
        detail2.setItemId(2L);
        detail2.setFromWarehouseId(1L);
        detail2.setFromStorageLocationId(1L);
        detail2.setToWarehouseId(2L);
        detail2.setToStorageLocationId(2L);
        detail2.setQuantityChange(new BigDecimal("20.750"));
        detail2.setStatus(SlipDetail.Status.PROCESSED);
        detail2.setNote("測試明細2");

        SlipDetail detail3 = new SlipDetail();
        detail3.setLineNumber(1);
        detail3.setSlipId(2L);
        detail3.setItemId(1L);
        detail3.setFromWarehouseId(2L);
        detail3.setFromStorageLocationId(2L);
        detail3.setToWarehouseId(3L);
        detail3.setToStorageLocationId(3L);
        detail3.setQuantityChange(new BigDecimal("5.250"));
        detail3.setStatus(SlipDetail.Status.CANCELLED);
        detail3.setNote("測試明細3");

        // 保存並刷新
        SlipDetail savedDetail1 = slipDetailRepository.save(detail1);
        SlipDetail savedDetail2 = slipDetailRepository.save(detail2);
        SlipDetail savedDetail3 = slipDetailRepository.save(detail3);
        entityManager.flush();

        // 測試基本查詢
        Optional<SlipDetail> foundDetail = slipDetailRepository.findById(savedDetail1.getId());
        assertThat(foundDetail).isPresent();
        assertThat(foundDetail.get().getLineNumber()).isEqualTo(1);
        assertThat(foundDetail.get().getQuantityChange()).isEqualTo(new BigDecimal("10.500"));

        // 測試 findBySlipId
        List<SlipDetail> detailsBySlipId = slipDetailRepository.findBySlipId(1L);
        assertThat(detailsBySlipId).hasSize(2);

        // 測試 findBySlipIdAndLineNumber
        SlipDetail detailBySlipIdAndLineNumber = slipDetailRepository.findBySlipIdAndLineNumber(1L, 1);
        assertThat(detailBySlipIdAndLineNumber).isNotNull();
        assertThat(detailBySlipIdAndLineNumber.getItemId()).isEqualTo(1L);

        // 測試 findByItemId
        List<SlipDetail> detailsByItemId = slipDetailRepository.findByItemId(1L);
        assertThat(detailsByItemId).hasSize(2);

        // 測試 findByFromWarehouseId
        List<SlipDetail> detailsByFromWarehouse = slipDetailRepository.findByFromWarehouseId(1L);
        assertThat(detailsByFromWarehouse).hasSize(2);

        // 測試 findByToWarehouseId
        List<SlipDetail> detailsByToWarehouse = slipDetailRepository.findByToWarehouseId(2L);
        assertThat(detailsByToWarehouse).hasSize(2);

        // 測試 findByFromStorageLocationId
        List<SlipDetail> detailsByFromStorageLocation = slipDetailRepository.findByFromStorageLocationId(1L);
        assertThat(detailsByFromStorageLocation).hasSize(2);

        // 測試 findByToStorageLocationId
        List<SlipDetail> detailsByToStorageLocation = slipDetailRepository.findByToStorageLocationId(2L);
        assertThat(detailsByToStorageLocation).hasSize(2);

        // 測試 findBySlipIdOrderByLineNumber
        List<SlipDetail> detailsOrderedByLineNumber = slipDetailRepository.findBySlipIdOrderByLineNumber(1L);
        assertThat(detailsOrderedByLineNumber).hasSize(2);
        assertThat(detailsOrderedByLineNumber.get(0).getLineNumber()).isEqualTo(1);
        assertThat(detailsOrderedByLineNumber.get(1).getLineNumber()).isEqualTo(2);

        // 測試 findBySlipIdAndItemId
        List<SlipDetail> detailsBySlipIdAndItemId = slipDetailRepository.findBySlipIdAndItemId(1L, 1L);
        assertThat(detailsBySlipIdAndItemId).hasSize(1);
        assertThat(detailsBySlipIdAndItemId.get(0).getLineNumber()).isEqualTo(1);

        // 測試 existsBySlipId
        boolean existsBySlipId = slipDetailRepository.existsBySlipId(1L);
        assertThat(existsBySlipId).isTrue();

        boolean notExistsBySlipId = slipDetailRepository.existsBySlipId(999L);
        assertThat(notExistsBySlipId).isFalse();

        // 測試 countBySlipId
        Long countBySlipId = slipDetailRepository.countBySlipId(1L);
        assertThat(countBySlipId).isEqualTo(2L);

        // 測試 countByItemId
        Long countByItemId = slipDetailRepository.countByItemId(1L);
        assertThat(countByItemId).isEqualTo(2L);

        // 測試 findMaxLineNumberBySlipId
        Integer maxLineNumber = slipDetailRepository.findMaxLineNumberBySlipId(1L);
        assertThat(maxLineNumber).isEqualTo(2);

        // 測試 findByWarehouseId
        List<SlipDetail> detailsByWarehouseId = slipDetailRepository.findByWarehouseId(2L);
        assertThat(detailsByWarehouseId).hasSize(3); // 2個作為目標倉庫，1個作為來源倉庫

        // 測試 findByStorageLocationId
        List<SlipDetail> detailsByStorageLocationId = slipDetailRepository.findByStorageLocationId(2L);
        assertThat(detailsByStorageLocationId).hasSize(3); // 2個作為目標儲位，1個作為來源儲位

        // 測試新增的狀態相關方法
        // 測試 findByStatus
        List<SlipDetail> pendingDetails = slipDetailRepository.findByStatus(SlipDetail.Status.PENDING);
        assertThat(pendingDetails).hasSize(1);
        assertThat(pendingDetails.get(0).getStatus()).isEqualTo(SlipDetail.Status.PENDING);

        List<SlipDetail> processedDetails = slipDetailRepository.findByStatus(SlipDetail.Status.PROCESSED);
        assertThat(processedDetails).hasSize(1);
        assertThat(processedDetails.get(0).getStatus()).isEqualTo(SlipDetail.Status.PROCESSED);

        List<SlipDetail> cancelledDetails = slipDetailRepository.findByStatus(SlipDetail.Status.CANCELLED);
        assertThat(cancelledDetails).hasSize(1);
        assertThat(cancelledDetails.get(0).getStatus()).isEqualTo(SlipDetail.Status.CANCELLED);

        // 測試 findBySlipIdAndStatus
        List<SlipDetail> slip1PendingDetails = slipDetailRepository.findBySlipIdAndStatus(1L, SlipDetail.Status.PENDING);
        assertThat(slip1PendingDetails).hasSize(1);
        assertThat(slip1PendingDetails.get(0).getLineNumber()).isEqualTo(1);

        List<SlipDetail> slip1ProcessedDetails = slipDetailRepository.findBySlipIdAndStatus(1L, SlipDetail.Status.PROCESSED);
        assertThat(slip1ProcessedDetails).hasSize(1);
        assertThat(slip1ProcessedDetails.get(0).getLineNumber()).isEqualTo(2);

        // 測試 findBySlipIdAndStatusOrderByLineNumber
        List<SlipDetail> slip1DetailsOrderedByLineNumber = slipDetailRepository.findBySlipIdAndStatusOrderByLineNumber(1L, SlipDetail.Status.PENDING);
        assertThat(slip1DetailsOrderedByLineNumber).hasSize(1);
        assertThat(slip1DetailsOrderedByLineNumber.get(0).getLineNumber()).isEqualTo(1);

        // 測試 countBySlipIdAndStatus
        Long pendingCountForSlip1 = slipDetailRepository.countBySlipIdAndStatus(1L, SlipDetail.Status.PENDING);
        assertThat(pendingCountForSlip1).isEqualTo(1L);

        Long processedCountForSlip1 = slipDetailRepository.countBySlipIdAndStatus(1L, SlipDetail.Status.PROCESSED);
        assertThat(processedCountForSlip1).isEqualTo(1L);

        Long cancelledCountForSlip1 = slipDetailRepository.countBySlipIdAndStatus(1L, SlipDetail.Status.CANCELLED);
        assertThat(cancelledCountForSlip1).isEqualTo(0L);

        // 測試 existsBySlipIdAndStatus
        boolean slip1HasPending = slipDetailRepository.existsBySlipIdAndStatus(1L, SlipDetail.Status.PENDING);
        assertThat(slip1HasPending).isTrue();

        boolean slip1HasCancelled = slipDetailRepository.existsBySlipIdAndStatus(1L, SlipDetail.Status.CANCELLED);
        assertThat(slip1HasCancelled).isFalse();

        boolean slip2HasCancelled = slipDetailRepository.existsBySlipIdAndStatus(2L, SlipDetail.Status.CANCELLED);
        assertThat(slip2HasCancelled).isTrue();

        // 測試空結果情況
        List<SlipDetail> emptyResult = slipDetailRepository.findBySlipId(999L);
        assertThat(emptyResult).isEmpty();

        SlipDetail nullResult = slipDetailRepository.findBySlipIdAndLineNumber(999L, 1);
        assertThat(nullResult).isNull();

        Integer nullMaxLineNumber = slipDetailRepository.findMaxLineNumberBySlipId(999L);
        assertThat(nullMaxLineNumber).isNull();
    }
}