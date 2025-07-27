package core.interface.controller;

import core.interface.controller.dto.ApiResponse;
import core.library.base.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 首頁控制器
 * 負責處理系統基本信息相關的 HTTP 請求
 */
@RestController
@RequestMapping("/api")
public class HomeController extends BaseController {

    /**
     * 系統首頁 API - GET 請求
     * 提供系統基本信息和狀態
     * 
     * @return 系統信息回應
     */
    @GetMapping("/index")
    public ResponseEntity<ApiResponse<Map<String, Object>>> index() {
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("systemName", "ERP 系統");
        systemInfo.put("version", "1.0.0");
        systemInfo.put("status", "運行中");
        systemInfo.put("currentTime", LocalDateTime.now());
        systemInfo.put("description", "企業資源規劃系統");
        
        return success(systemInfo);
    }
}