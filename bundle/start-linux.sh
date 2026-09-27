#!/usr/bin/env bash
# 2009scape 正體中文單機版啟動腳本（Linux x64）
set -euo pipefail
DIR="$(cd -- "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR/game"

JAVA="$DIR/jre/bin/java"
if [ ! -x "$JAVA" ]; then
  JAVA=java
fi

echo "[1/2] 正在啟動離線伺服器..."
"$JAVA" -Xmx2G -jar server.jar &
SERVER_PID=$!
trap 'kill "$SERVER_PID" 2>/dev/null || true' EXIT

echo "    等待伺服器載入世界（約 25 秒）..."
sleep 25

echo "[2/2] 啟動遊戲客戶端..."
"$JAVA" -Xmx1G -cp client.jar rt4.client
