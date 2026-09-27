# rt4-client-zh-tw — 2009scape 正體中文用戶端

一個業餘專案：把 [2009scape](https://2009scape.org)（社群開源復刻 2009 年風貌 RuneScape 的專案）的用戶端與伺服器漢化為正體中文。這裡是漢化後的**用戶端 fork**（`zh-tw` 分支），發佈裡包含一個免安裝的一鍵離線單機包。

## 這是什麼遊戲

2009scape 走的是比官方 OSRS 更早、2009 年前後的介面與手感：2D 點地走、技能樹、老 Quest、自由交易，AGPL-3.0 全開源。它本身只有英/德/法語，沒有中文——這套漢化就是補這個缺。官方線上服免費遊玩（[2009scape.org](https://2009scape.org)）。

## 該下載哪個

到 [Releases](https://github.com/AosakiReiya/rt4-client-zh-tw/releases) 按需求挑：

| 想要… | 檔案 |
|---|---|
| **馬上玩（多數人選這個）** | `2009scape-zh-tw-singleplayer-win64.zip`（Linux 用 `linux64`）。約 170MB，解壓後雙擊 `start-game.bat` 即玩：內含 Java、漢化伺服器與遊戲資料，離線單機，免裝免架服免設定 |
| 已有 launcher、只想換中文客戶端 | `client-zh-tw.jar`（附 `.sha256`，沿用官方 CI 慣例）。注意遊戲內訊息翻譯需連漢化伺服器，僅換客戶端只有介面與名稱是中文 |
| 自己架漢化伺服器 | 請見姊妹倉 [2009scape-zh-tw-server](https://github.com/AosakiReiya/2009scape-zh-tw-server) 的 Releases（`server-zh-tw.jar` + `server-data-zh-tw.zip`） |

單機包在打包時會自動抓取 server 倉的最新 release 組裝，所以發佈順序是先 server tag、再 client tag。

## 漢化了什麼

- **用戶端**：字串層改造（原 ISO-8859-1 → UTF-16 分流）、AWT 中文字渲染接進軟體渲染管線、登入/聊天/右鍵選單等 UI 語系、約 4,000 條介面字串、道具與 NPC 名稱雙語顯示
- **伺服器端**：訊息與對話即時翻譯、動態拼接句模板（"你需要…等級…"類）、道具/NPC/商店名稱漢化（詳見 server 倉）
- **詞表**：數萬條文字由本地 LLM（gemma）批次翻譯，以九千餘條術語表約束一致性

## 已知限制

- 翻譯是 AI 產出，未逐條人工審校，術語不一致、誤翻、少量漏翻的長句仍存在，歡迎開 issue 回報
- 聊天輸入框不支援打中文（顯示中文沒問題）
- OpenGL 渲染模式下中文以佔位寬度顯示（預設軟體渲染不受影響）
- 官方線上伺服器不會是中文；要完整效果請玩單機包或自建漢化服

## 開發建置

JDK 11，Gradle wrapper：

```bash
./gradlew :client:build -x test -x javadoc
# 產出 client/build/libs/client-1.0.0.jar
```

發佈：打 `v*` tag → GitHub Actions 自動編譯並建立 Release（含單機包組裝）。

## Credits

- 基底用戶端：<https://gitlab.com/2009scape/rt4-client>（2009scape 團隊；反編譯與重建歷程請見其 README 與 <https://github.com/Pazaz/RT4-Client>）
- 伺服器：<https://gitlab.com/2009scape/2009scape>
- 單機包遊戲 cache 與 Windows JRE：官方 <https://gitlab.com/2009scape/singleplayer/windows>
- Linux JRE：[Eclipse Temurin](https://adoptium.net)（Adoptium）
- 授權：AGPL-3.0，與上游一致。本漢化為社群改作，與 Jagex、2009scape 官方團隊無隸屬關係。
