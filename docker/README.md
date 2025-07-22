# ERP 系統 Docker 環境

## 快速開始

### 使用腳本啟動 (推薦)
直接執行 `restart.bat` 腳本即可停止現有容器並重新啟動。

### 使用 Docker Compose 命令
```bash
# 啟動服務
docker-compose -f docker-compose.yml -p erp up -d

# 停止服務
docker-compose -f docker-compose.yml -p erp down
```

### IDE 配置
在 IDE 中配置 Docker 運行環境:
- 將 Docker 插件指向 `docker-compose.yml` 文件
- 設置工作目錄為 `docker` 目錄

## 服務訪問
- MySQL: localhost: 30306 (用戶: erp_user, 密碼: erp_password)
- phpMyAdmin: http://localhost:30310
