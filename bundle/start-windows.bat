@echo off
chcp 65001 >nul
rem 2009scape 正體中文單機版啟動腳本（Windows x64）
cd /d "%~dp0game"

set "JAVA=%~dp0jre\bin\java.exe"
if not exist "%JAVA%" set "JAVA=java"

echo [1/2] 正在啟動離線伺服器...
echo     伺服器視窗於遊戲期間請勿關閉。
start "2009scape-zh-tw-server" "%JAVA%" -Xmx2G -jar server.jar

echo     等待伺服器載入世界（約 25 秒）...
timeout /t 25 /nobreak >nul

echo [2/2] 啟動遊戲客戶端...
start "2009scape-zh-tw-client" "%JAVA%" -Xmx1G -cp client.jar rt4.client
exit /b 0
