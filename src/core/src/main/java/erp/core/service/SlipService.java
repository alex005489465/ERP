package erp.core.service;

import erp.core.dto.SlipDetailDto;
import erp.core.entity.Slip;
import erp.core.entity.SlipDetail;
import erp.core.entity.StorageLocation;
import erp.core.repository.SlipRepository;
import erp.core.repository.SlipDetailRepository;
import erp.core.repository.StorageLocationRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 單據服務類
 * 提供單據的基本CRUD操作和業務邏輯
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SlipService {

    private final SlipRepository slipRepository;
    private final SlipDetailRepository slipDetailRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final WarehouseManagementService warehouseManagementService;


    /**
     * 單據類型枚舉
     */
    @Getter
    public enum SlipType {
        INBOUND((byte) 1, "入庫單"),
        OUTBOUND((byte) 2, "出庫單"),
        TRANSFER((byte) 3, "轉倉單"),
        FREEZE((byte) 4, "凍結單"),
        SCRAP((byte) 5, "報廢單");

        private final byte code;
        private final String description;

        SlipType(byte code, String description) {
            this.code = code;
            this.description = description;
        }

        public static SlipType fromCode(byte code) {
            for (SlipType type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            throw new IllegalArgumentException("未知的單據類型代碼: " + code);
        }
    }

    /**
     * 單據狀態枚舉
     */
    @Getter
    public enum SlipStatus {
        DRAFT((byte) 0, "草稿"),
        COMPLETED((byte) 1, "完成"),
        CANCELLED((byte) 2, "取消");

        private final byte code;
        private final String description;

        SlipStatus(byte code, String description) {
            this.code = code;
            this.description = description;
        }

        public static SlipStatus fromCode(byte code) {
            for (SlipStatus status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException("未知的單據狀態代碼: " + code);
        }
    }

    //region CRUD 操作

    /**
     * 創建新單據
     */
    public Slip createSlip(SlipType slipType, Long createdBy) {
        log.info("創建新單據 - 類型: {}, 建立人: {}", slipType.getDescription(), createdBy);
        
        Slip slip = new Slip();
        slip.setSlipsType(slipType.getCode());
        slip.setCreatedBy(createdBy);
        slip.setStatus(SlipStatus.DRAFT.getCode());
        
        Slip savedSlip = slipRepository.save(slip);
        log.info("成功創建單據 - ID: {}", savedSlip.getId());
        
        return savedSlip;
    }

    /**
     * 創建新單據並解析單據資料到單據資料表
     */
    public Slip createSlip(SlipType slipType, Long createdBy, List<SlipDetailDto> slipDetails) {
        log.info("創建新單據並解析單據資料 - 類型: {}, 建立人: {}, 明細數量: {}", 
                slipType.getDescription(), createdBy, slipDetails != null ? slipDetails.size() : 0);
        
        // 創建主單據
        Slip slip = new Slip();
        slip.setSlipsType(slipType.getCode());
        slip.setCreatedBy(createdBy);
        slip.setStatus(SlipStatus.DRAFT.getCode());
        
        Slip savedSlip = slipRepository.save(slip);
        log.info("成功創建單據 - ID: {}", savedSlip.getId());
        
        // 解析並保存單據明細資料
        if (slipDetails != null && !slipDetails.isEmpty()) {
            parseAndSaveSlipDetails(savedSlip.getId(), slipDetails);
        }
        
        return savedSlip;
    }

    /**
     * 解析單據資料到單據資料表
     */
    private void parseAndSaveSlipDetails(Long slipId, List<SlipDetailDto> slipDetails) {
        log.info("開始解析單據資料到單據資料表 - 單據ID: {}, 明細數量: {}", slipId, slipDetails.size());
        
        for (SlipDetailDto detailData : slipDetails) {
            // 驗證必要欄位
            if (detailData.getItemId() == null) {
                throw new IllegalArgumentException("商品ID不能為空");
            }
            if (detailData.getQuantityChange() == null) {
                throw new IllegalArgumentException("異動數量不能為空");
            }
            
            // 創建SlipDetail實體
            SlipDetail slipDetail = new SlipDetail();
            slipDetail.setSlipId(slipId);
            slipDetail.setLineNumber(detailData.getLineNumber());
            slipDetail.setItemId(detailData.getItemId());
            slipDetail.setFromWarehouseId(detailData.getFromWarehouseId());
            slipDetail.setFromStorageLocationId(detailData.getFromStorageLocationId());
            slipDetail.setToWarehouseId(detailData.getToWarehouseId());
            slipDetail.setToStorageLocationId(detailData.getToStorageLocationId());
            slipDetail.setQuantityChange(detailData.getQuantityChange());
            slipDetail.setStatus(SlipDetail.Status.PENDING);
            slipDetail.setNote(detailData.getNote());
            
            // 保存到資料庫
            SlipDetail savedDetail = slipDetailRepository.save(slipDetail);
            log.debug("成功保存單據明細 - 明細ID: {}, 項次: {}, 商品ID: {}, 數量: {}", 
                    savedDetail.getId(), savedDetail.getLineNumber(), 
                    savedDetail.getItemId(), savedDetail.getQuantityChange());
        }
        
        log.info("完成解析單據資料到單據資料表 - 單據ID: {}, 已保存明細數量: {}", slipId, slipDetails.size());
    }

    /**
     * 根據ID查詢單據
     */
    @Transactional(readOnly = true)
    public Optional<Slip> getSlipById(Long id) {
        log.debug("查詢單據 - ID: {}", id);
        return slipRepository.findById(id);
    }

    /**
     * 查詢所有單據
     */
    @Transactional(readOnly = true)
    public List<Slip> getAllSlips() {
        log.debug("查詢所有單據");
        return slipRepository.findAll();
    }

    /**
     * 更新單據
     * @deprecated 此方法已棄用
     */
    @Deprecated
    public Slip updateSlip(Long id, SlipType slipType, Long createdBy, SlipStatus status) {
        log.info("更新單據 - ID: {}", id);
        
        Slip slip = slipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到指定的單據: " + id));
        
        if (slipType != null) {
            slip.setSlipsType(slipType.getCode());
        }
        if (createdBy != null) {
            slip.setCreatedBy(createdBy);
        }
        if (status != null) {
            slip.setStatus(status.getCode());
        }
        
        Slip updatedSlip = slipRepository.save(slip);
        log.info("成功更新單據 - ID: {}", updatedSlip.getId());
        
        return updatedSlip;
    }

    /**
     * 刪除單據
     */
    public void deleteSlip(Long id) {
        log.info("刪除單據 - ID: {}", id);
        
        if (!slipRepository.existsById(id)) {
            throw new IllegalArgumentException("找不到指定的單據: " + id);
        }
        
        slipRepository.deleteById(id);
        log.info("成功刪除單據 - ID: {}", id);
    }

    //endregion

    //region 查詢操作

    /**
     * 根據單據類型查詢
     */
    @Transactional(readOnly = true)
    public List<Slip> getSlipsByType(SlipType slipType) {
        log.debug("根據類型查詢單據 - 類型: {}", slipType.getDescription());
        return slipRepository.findBySlipsType(slipType.getCode());
    }

    /**
     * 根據建立人查詢
     */
    @Transactional(readOnly = true)
    public List<Slip> getSlipsByCreatedBy(Long createdBy) {
        log.debug("根據建立人查詢單據 - 建立人: {}", createdBy);
        return slipRepository.findByCreatedBy(createdBy);
    }

    /**
     * 根據狀態查詢
     */
    @Transactional(readOnly = true)
    public List<Slip> getSlipsByStatus(SlipStatus status) {
        log.debug("根據狀態查詢單據 - 狀態: {}", status.getDescription());
        return slipRepository.findByStatus(status.getCode());
    }

    /**
     * 根據類型和狀態查詢
     */
    @Transactional(readOnly = true)
    public List<Slip> getSlipsByTypeAndStatus(SlipType slipType, SlipStatus status) {
        log.debug("根據類型和狀態查詢單據 - 類型: {}, 狀態: {}", 
                slipType.getDescription(), status.getDescription());
        return slipRepository.findBySlipsTypeAndStatus(slipType.getCode(), status.getCode());
    }

    /**
     * 根據時間範圍查詢
     */
    @Transactional(readOnly = true)
    public List<Slip> getSlipsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("根據時間範圍查詢單據 - 開始: {}, 結束: {}", startDate, endDate);
        return slipRepository.findByCreatedAtBetween(startDate, endDate);
    }

    /**
     * 獲取指定類型的最新10筆單據
     */
    @Transactional(readOnly = true)
    public List<Slip> getLatestSlipsByType(SlipType slipType) {
        log.debug("獲取最新單據 - 類型: {}", slipType.getDescription());
        return slipRepository.findTop10BySlipsTypeOrderByCreatedAtDesc(slipType.getCode());
    }

    /**
     * 獲取指定建立人的最新10筆單據
     */
    @Transactional(readOnly = true)
    public List<Slip> getLatestSlipsByCreatedBy(Long createdBy) {
        log.debug("獲取最新單據 - 建立人: {}", createdBy);
        return slipRepository.findTop10ByCreatedByOrderByCreatedAtDesc(createdBy);
    }

    //endregion

    //region 業務操作

    /**
     * 完成單據
     * 改為成功狀態，並調用庫存服務，使用事務和異步處理
     */
    @Transactional
    public Slip completeSlip(Long id) {
        log.info("完成單據 - ID: {}", id);
        
        Slip slip = slipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到指定的單據: " + id));
        
        if (slip.getStatus() == SlipStatus.COMPLETED.getCode()) {
            throw new IllegalStateException("單據已經是完成狀態");
        }
        
        if (slip.getStatus() == SlipStatus.CANCELLED.getCode()) {
            throw new IllegalStateException("已取消的單據無法完成");
        }
        
        // 先更新單據狀態為完成
        slip.setStatus(SlipStatus.COMPLETED.getCode());
        Slip completedSlip = slipRepository.save(slip);
        
        // 根據單據類型異步調用相應的庫存服務
        SlipType slipType = SlipType.fromCode(completedSlip.getSlipsType());
        processInventoryOperationAsync(completedSlip, slipType);
        
        log.info("成功完成單據 - ID: {}, 類型: {}", completedSlip.getId(), slipType.getDescription());
        return completedSlip;
    }
    
    /**
     * 根據單據類型異步處理庫存操作
     * 逐一處理 SlipDetail 記錄到 StockMovement，並更新 SlipDetail 的狀態
     */
    private void processInventoryOperationAsync(Slip slip, SlipType slipType) {
        log.info("開始處理單據庫存操作 - 單據ID: {}, 類型: {}", slip.getId(), slipType.getDescription());
        
        // 獲取待處理的單據明細
        List<SlipDetail> slipDetails = slipDetailRepository.findBySlipIdAndStatus(slip.getId(), SlipDetail.Status.PENDING);
        
        if (slipDetails.isEmpty()) {
            log.info("單據沒有待處理的明細 - 單據ID: {}", slip.getId());
            return;
        }
        
        log.info("找到 {} 筆待處理明細 - 單據ID: {}", slipDetails.size(), slip.getId());
        
        // 逐一處理每個 SlipDetail
        for (SlipDetail slipDetail : slipDetails) {
            processSlipDetailAsync(slipDetail, slipType);
        }
        
        log.info("完成單據庫存操作處理 - 單據ID: {}, 處理明細數: {}", slip.getId(), slipDetails.size());
    }
    
    /**
     * 異步處理單個 SlipDetail 記錄
     */
    @Async
    public void processSlipDetailAsync(SlipDetail slipDetail, SlipType slipType) {
        log.info("開始處理單據明細 - 明細ID: {}, 項次: {}, 類型: {}", 
                slipDetail.getId(), slipDetail.getLineNumber(), slipType.getDescription());
        
        try {
            // 更新狀態為處理中（可選，用於追蹤）
            // slipDetail.setStatus(SlipDetail.Status.PROCESSING);
            // slipDetailRepository.save(slipDetail);
            
            String note = String.format("單據完成觸發 - 單據ID: %d, 明細ID: %d, 項次: %d", 
                    slipDetail.getSlipId(), slipDetail.getId(), slipDetail.getLineNumber());
            
            switch (slipType) {
                case INBOUND:
                    processInboundSlipDetail(slipDetail, note);
                    break;
                case OUTBOUND:
                    processOutboundSlipDetail(slipDetail, note);
                    break;
                case TRANSFER:
                    processTransferSlipDetail(slipDetail, note);
                    break;
                case FREEZE:
                    processFreezeSlipDetail(slipDetail, note);
                    break;
                case SCRAP:
                    processScrapSlipDetail(slipDetail, note);
                    break;
                default:
                    log.warn("未知的單據類型，跳過處理 - 明細ID: {}, 類型: {}", slipDetail.getId(), slipType);
                    return;
            }
            
            // 更新 SlipDetail 狀態為已處理
            slipDetail.setStatus(SlipDetail.Status.PROCESSED);
            slipDetailRepository.save(slipDetail);
            
            log.info("成功處理單據明細 - 明細ID: {}, 項次: {}", slipDetail.getId(), slipDetail.getLineNumber());
            
        } catch (Exception e) {
            log.error("處理單據明細失敗 - 明細ID: {}, 項次: {}, 錯誤: {}", 
                    slipDetail.getId(), slipDetail.getLineNumber(), e.getMessage(), e);
            
            // 更新 SlipDetail 狀態為失敗
            try {
                slipDetail.setStatus(SlipDetail.Status.FAILED);
                slipDetailRepository.save(slipDetail);
            } catch (Exception saveException) {
                log.error("更新明細狀態失敗 - 明細ID: {}, 錯誤: {}", slipDetail.getId(), saveException.getMessage());
            }
        }
    }
    
    /**
     * 處理入庫類型的 SlipDetail
     */
    private void processInboundSlipDetail(SlipDetail slipDetail, String note) {
        String toLocation = getLocationCodeById(slipDetail.getToStorageLocationId());
        warehouseManagementService.inbound(slipDetail.getItemId(), toLocation, slipDetail.getQuantityChange(), note);
        log.debug("入庫操作完成 - 明細ID: {}, 商品ID: {}, 位置: {}, 數量: {}", 
                slipDetail.getId(), slipDetail.getItemId(), toLocation, slipDetail.getQuantityChange());
    }
    
    /**
     * 處理出庫類型的 SlipDetail
     */
    private void processOutboundSlipDetail(SlipDetail slipDetail, String note) {
        String fromLocation = getLocationCodeById(slipDetail.getFromStorageLocationId());
        warehouseManagementService.outbound(slipDetail.getItemId(), fromLocation, slipDetail.getQuantityChange(), note);
        log.debug("出庫操作完成 - 明細ID: {}, 商品ID: {}, 位置: {}, 數量: {}", 
                slipDetail.getId(), slipDetail.getItemId(), fromLocation, slipDetail.getQuantityChange());
    }
    
    /**
     * 處理轉庫類型的 SlipDetail
     */
    private void processTransferSlipDetail(SlipDetail slipDetail, String note) {
        String fromLocation = getLocationCodeById(slipDetail.getFromStorageLocationId());
        String toLocation = getLocationCodeById(slipDetail.getToStorageLocationId());
        warehouseManagementService.transfer(slipDetail.getItemId(), fromLocation, toLocation, slipDetail.getQuantityChange(), note);
        log.debug("轉庫操作完成 - 明細ID: {}, 商品ID: {}, 從 {} 轉至 {}, 數量: {}", 
                slipDetail.getId(), slipDetail.getItemId(), fromLocation, toLocation, slipDetail.getQuantityChange());
    }
    
    /**
     * 處理凍結類型的 SlipDetail
     */
    private void processFreezeSlipDetail(SlipDetail slipDetail, String note) {
        String fromLocation = getLocationCodeById(slipDetail.getFromStorageLocationId());
        warehouseManagementService.freeze(slipDetail.getItemId(), fromLocation, slipDetail.getQuantityChange(), note);
        log.debug("凍結操作完成 - 明細ID: {}, 商品ID: {}, 位置: {}, 數量: {}", 
                slipDetail.getId(), slipDetail.getItemId(), fromLocation, slipDetail.getQuantityChange());
    }
    
    /**
     * 處理報廢類型的 SlipDetail
     */
    private void processScrapSlipDetail(SlipDetail slipDetail, String note) {
        String fromLocation = getLocationCodeById(slipDetail.getFromStorageLocationId());
        warehouseManagementService.scrap(slipDetail.getItemId(), fromLocation, slipDetail.getQuantityChange(), note);
        log.debug("報廢操作完成 - 明細ID: {}, 商品ID: {}, 位置: {}, 數量: {}", 
                slipDetail.getId(), slipDetail.getItemId(), fromLocation, slipDetail.getQuantityChange());
    }
    
    /**
     * 根據儲位ID獲取位置編號
     */
    private String getLocationCodeById(Long storageLocationId) {
        if (storageLocationId == null) {
            throw new IllegalArgumentException("儲位ID不能為空");
        }
        
        StorageLocation storageLocation = storageLocationRepository.findById(storageLocationId)
                .orElseThrow(() -> new IllegalArgumentException("找不到指定的儲位: " + storageLocationId));
        
        if (storageLocation.getCode() == null || storageLocation.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("儲位編號不能為空 - 儲位ID: " + storageLocationId);
        }
        
        return storageLocation.getCode();
    }

    /**
     * 取消單據
     */
    public Slip cancelSlip(Long id) {
        log.info("取消單據 - ID: {}", id);
        
        Slip slip = slipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到指定的單據: " + id));
        
        if (slip.getStatus() == SlipStatus.CANCELLED.getCode()) {
            throw new IllegalStateException("單據已經是取消狀態");
        }
        
        if (slip.getStatus() == SlipStatus.COMPLETED.getCode()) {
            throw new IllegalStateException("已完成的單據無法取消");
        }
        
        slip.setStatus(SlipStatus.CANCELLED.getCode());
        Slip cancelledSlip = slipRepository.save(slip);
        
        log.info("成功取消單據 - ID: {}", cancelledSlip.getId());
        return cancelledSlip;
    }

    //endregion

    //region 統計操作

    /**
     * 統計指定類型和狀態的單據數量
     */
    @Transactional(readOnly = true)
    public Long countSlipsByTypeAndStatus(SlipType slipType, SlipStatus status) {
        log.debug("統計單據數量 - 類型: {}, 狀態: {}", 
                slipType.getDescription(), status.getDescription());
        return slipRepository.countBySlipsTypeAndStatus(slipType.getCode(), status.getCode());
    }

    /**
     * 統計指定建立人的單據數量
     */
    @Transactional(readOnly = true)
    public Long countSlipsByCreatedBy(Long createdBy) {
        log.debug("統計單據數量 - 建立人: {}", createdBy);
        return slipRepository.countByCreatedBy(createdBy);
    }

    /**
     * 檢查指定建立人是否有未完成的單據
     */
    @Transactional(readOnly = true)
    public boolean hasIncompleteSlips(Long createdBy) {
        log.debug("檢查未完成單據 - 建立人: {}", createdBy);
        return slipRepository.existsByCreatedByAndStatusNot(createdBy, SlipStatus.COMPLETED.getCode());
    }

    /**
     * 獲取所有不同的單據類型
     */
    @Transactional(readOnly = true)
    public List<Byte> getAllDistinctSlipTypes() {
        log.debug("獲取所有單據類型");
        return slipRepository.findAllDistinctSlipsTypes();
    }

    //endregion
}