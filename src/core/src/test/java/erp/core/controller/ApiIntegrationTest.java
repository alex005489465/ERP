package erp.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import erp.core.dto.ApiRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        
        ApiRequest request = new ApiRequest("create", data);
        
        mockMvc.perform(post("/api/warehouse/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
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
        
        ApiRequest request = new ApiRequest("create", data);
        
        mockMvc.perform(post("/api/warehouse/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
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
        
        ApiRequest request = new ApiRequest("getAll", null);
        
        mockMvc.perform(post("/api/warehouse/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
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
        
        ApiRequest request = new ApiRequest("search", data);
        
        mockMvc.perform(post("/api/warehouse/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("搜尋成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.errorCode").isEmpty());
    }
    
    @Test
    void testUnsupportedAction() throws Exception {
        ApiRequest request = new ApiRequest("invalidAction", null);
        
        mockMvc.perform(post("/api/warehouse/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("不支援的操作類型: invalidAction"))
                .andExpect(jsonPath("$.errorCode").value("UNSUPPORTED_ACTION"));
    }
    
    // ========== 庫存查詢API測試 ==========
    
    @Test
    void testStockGetZeroStocks() throws Exception {
        ApiRequest request = new ApiRequest("getZeroStocks", null);
        
        mockMvc.perform(post("/api/warehouse/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查詢成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.errorCode").isEmpty());
    }
    
    @Test
    void testStockGetLowStocks() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("threshold", 10);
        
        ApiRequest request = new ApiRequest("getLowStocks", data);
        
        mockMvc.perform(post("/api/warehouse/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查詢成功"))
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
        
        ApiRequest request = new ApiRequest("getTotalStock", data);
        
        mockMvc.perform(post("/api/warehouse/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
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
        
        ApiRequest request = new ApiRequest("inbound", data);
        
        mockMvc.perform(post("/api/warehouse/operation")
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
        
        ApiRequest request = new ApiRequest("outbound", data);
        
        mockMvc.perform(post("/api/warehouse/operation")
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
        
        ApiRequest request = new ApiRequest("transfer", data);
        
        mockMvc.perform(post("/api/warehouse/operation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
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
        
        ApiRequest request = new ApiRequest("outbound", data);
        
        mockMvc.perform(post("/api/warehouse/operation")
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
        ApiRequest request = new ApiRequest("getRecent", null);
        
        mockMvc.perform(post("/api/warehouse/movement")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
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
        
        ApiRequest request = new ApiRequest("getByItem", data);
        
        mockMvc.perform(post("/api/warehouse/movement")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
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
        
        ApiRequest request = new ApiRequest("getByLocation", data);
        
        mockMvc.perform(post("/api/warehouse/movement")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查詢成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.errorCode").isEmpty());
    }
    
    // ========== 錯誤處理測試 ==========
    
    @Test
    void testInvalidJsonFormat() throws Exception {
        String invalidJson = "{\"action\": \"create\", \"data\": {\"name\": \"test\", \"unit\":}}";
        mockMvc.perform(post("/api/warehouse/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("請求格式錯誤，請檢查JSON格式"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_ARGUMENT"));
    }
    
    @Test
    void testMissingAction() throws Exception {
        ApiRequest request = new ApiRequest(null, null);
        
        mockMvc.perform(post("/api/warehouse/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("操作類型不能為空"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_ARGUMENT"));
    }
}