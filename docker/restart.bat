@echo off
chcp 65001 > nul
setlocal

@REM 設定 compose 檔名與專案名稱（可依需求修改）
set COMPOSE_FILE=docker-compose.yml
set PROJECT_NAME=erp

@REM echo.
@REM echo "🛑 正在停止並移除容器..."
@REM docker-compose -f %COMPOSE_FILE% -p %PROJECT_NAME% down
@REM docker-compose -f %COMPOSE_FILE% down


echo.
echo "🚀 正在啟動容器..."
@REM docker-compose -f %COMPOSE_FILE% -p %PROJECT_NAME% up -d
docker-compose -f %COMPOSE_FILE% up -d

echo.
echo "✅ 完成！"

endlocal
pause
