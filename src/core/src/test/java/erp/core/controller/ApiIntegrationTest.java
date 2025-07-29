package erp.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import erp.core.entity.Item;
import erp.core.service.WarehouseManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

/**
 * API集成測試
 * 測試所有四個API端點的功能
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ApiIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private WarehouseManagementService warehouseService;
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }
    
    // ========== 商品管理API測試 ==========
    
    @Test
    void testItemCreate() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "測試商品");
        data.put("unit", "個");
        
        mockMvc.perform(post("/api/warehouse/item/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("商品創建成功"))
                .andExpect(jsonPath("$.data.name").value("測試商品"))
                .andExpect(jsonPath("$.data.unit").value("個"))
                .andExpect(jsonPath("$.errorCode").isEmpty());
    }
    
    @Test
    void testItemCreateWithMissingName() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("unit", "個");
        
        mockMvc.perform(post("/api/warehouse/item/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("商品名稱不能為空"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_ARGUMENT"));
    }
    
    @Test
    void testItemGetAll() throws Exception {
        // 先創建一個商品
        warehouseService.createItem("測試商品1", "個");
        warehouseService.createItem("測試商品2", "台");
        
        mockMvc.perform(post("/api/warehouse/item/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查詢成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.errorCode").isEmpty());
    }
    
    @Test
    void testItemSearch() throws Exception {
        // 先創建一個商品
        warehouseService.createItem("iPhone 15", "台");
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "iPhone");
        
        mockMvc.perform(post("/api/warehouse/item/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("搜尋成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.errorCode").isEmpty());
    }
    
    @Test
    void testUnsupportedAction() throws Exception {
        // This test is no longer relevant since we don't have action-based endpoints
        // Instead, test accessing a non-existent endpoint
        mockMvc.perform(post("/api/warehouse/item/invalidAction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().is5xxServerError());
    }
    
    // ========== 庫存查詢API測試 ==========
    
    @Test
    void testStockGetZeroStocks() throws Exception {
        mockMvc.perform(post("/api/warehouse/stock/lowAndZeroStocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查詢零庫存成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.errorCode").isEmpty());
    }
    
    @Test
    void testStockGetLowStocks() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("threshold", "10");
        
        mockMvc.perform(post("/api/warehouse/stock/lowAndZeroStocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查詢低庫存成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.errorCode").isEmpty());
    }
    
    @Test
    void testStockGetTotalStock() throws Exception {
        // 先創建商品和庫存
        Item item = warehouseService.createItem("測試商品", "個");
        warehouseService.inbound(item.getId(), "A001", new BigDecimal("100"), "測試入庫");
        
        Map<String, Object> data = new HashMap<>();
        data.put("itemId", item.getId());
        
        mockMvc.perform(post("/api/warehouse/stock/totalStock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查詢成功"))
                .andExpect(jsonPath("$.data").value(100))
                .andExpect(jsonPath("$.errorCode").isEmpty());
    }
    
    // ========== 庫存操作API測試 ==========
    
    @Test
    void testOperationInbound() throws Exception {
        // 先創建商品
        Item item = warehouseService.createItem("測試商品", "個");
        
        Map<String, Object> data = new HashMap<>();
        data.put("itemId", item.getId());
        data.put("location", "A001");
        data.put("quantity", 100);
        data.put("note", "測試入庫");
        
        Map<String, Object> request = new HashMap<>();
        request.put("action", "inbound");
        request.put("data", data);
        
        mockMvc.perform(post("/api/warehouse/operation/operation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("入庫操作成功"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errorCode").isEmpty());
    }
    
    @Test
    void testOperationOutbound() throws Exception {
        // 先創建商品和庫存
        Item item = warehouseService.createItem("測試商品", "個");
        warehouseService.inbound(item.getId(), "A001", new BigDecimal("100"), "測試入庫");
        
        Map<String, Object> data = new HashMap<>();
        data.put("itemId", item.getId());
        data.put("location", "A001");
        data.put("quantity", 50);
        data.put("note", "測試出庫");
        
        Map<String, Object> request = new HashMap<>();
        request.put("action", "outbound");
        request.put("data", data);
        
        mockMvc.perform(post("/api/warehouse/operation/operation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("出庫操作成功"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errorCode").isEmpty());
    }
    
    @Test
    void testOperationTransfer() throws Exception {
        // 先創建商品和庫存
        Item item = warehouseService.createItem("測試商品", "個");
        warehouseService.inbound(item.getId(), "A001", new BigDecimal("100"), "測試入庫");
        
        Map<String, Object> data = new HashMap<>();
        data.put("itemId", item.getId());
        data.put("fromLocation", "A001");
        data.put("toLocation", "B002");
        data.put("quantity", 30);
        data.put("note", "測試轉庫");
        
        mockMvc.perform(post("/api/warehouse/operation/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("轉庫操作成功"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errorCode").isEmpty());
    }
    
    @Test
    void testOperationInsufficientStock() throws Exception {
        // 先創建商品但不入庫
        Item item = warehouseService.createItem("測試商品", "個");
        
        Map<String, Object> data = new HashMap<>();
        data.put("itemId", item.getId());
        data.put("location", "A001");
        data.put("quantity", 50);
        
        Map<String, Object> request = new HashMap<>();
        request.put("action", "outbound");
        request.put("data", data);
        
        mockMvc.perform(post("/api/warehouse/operation/operation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("庫存不足")))
                .andExpect(jsonPath("$.errorCode").value("INSUFFICIENT_STOCK"));
    }
    
    // ========== 異動記錄API測試 ==========
    
    @Test
    void testMovementGetRecent() throws Exception {
        mockMvc.perform(post("/api/warehouse/movement/recent")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查詢成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.errorCode").isEmpty());
    }
    
    @Test
    void testMovementGetByItem() throws Exception {
        // 先創建商品和進行操作
        Item item = warehouseService.createItem("測試商品", "個");
        warehouseService.inbound(item.getId(), "A001", new BigDecimal("100"), "測試入庫");
        
        Map<String, Object> data = new HashMap<>();
        data.put("itemId", item.getId());
        
        mockMvc.perform(post("/api/warehouse/movement/byItem")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查詢成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.errorCode").isEmpty());
    }
    
    @Test
    void testMovementGetByLocation() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("location", "A001");
        
        mockMvc.perform(post("/api/warehouse/movement/byLocation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查詢成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.errorCode").isEmpty());
    }
    
    // ========== 錯誤處理測試 ==========
    
    @Test
    void testInvalidJsonFormat() throws Exception {
        String invalidJson = "{\"name\": \"test\", \"unit\":}";
        mockMvc.perform(post("/api/warehouse/item/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("請求格式錯誤，請檢查JSON格式"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_ARGUMENT"));
    }
    
    @Test
    void testMissingActionInOperation() throws Exception {
        // Test missing action in operation endpoint
        Map<String, Object> request = new HashMap<>();
        request.put("data", new HashMap<>());
        
        mockMvc.perform(post("/api/warehouse/operation/operation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("操作類型不能為空"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_ARGUMENT"));
    }
}