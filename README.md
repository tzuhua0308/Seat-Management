# 玉山銀行 員工座位系統

## 專案架構

```
seating-system/
├── backend/                          # Spring Boot 後端
│   ├── pom.xml
│   ├── DB/
│   │   ├── 01_DDL.sql               # 建立資料表
│   │   ├── 02_DML.sql               # 初始測試資料
│   │   └── 03_StoredProcedures.sql  # Stored Procedures
│   └── src/main/java/com/esun/seating/
│       ├── controller/              # 展示層
│       │   └── SeatingController.java
│       ├── service/                 # 業務層
│       │   └── SeatingService.java
│       ├── repository/              # 資料層
│       │   ├── SeatingChartRepository.java
│       │   └── EmployeeRepository.java
│       ├── model/                   # 資料模型
│       │   ├── Employee.java
│       │   └── SeatingChart.java
│       ├── dto/                     # 資料傳輸物件
│       │   ├── SeatAssignRequest.java
│       │   └── FloorSeatsResponse.java
│       └── common/                  # 共用層
│           ├── response/ApiResponse.java
│           ├── exception/
│           │   ├── BusinessException.java
│           │   └── GlobalExceptionHandler.java
│           └── util/XssUtil.java
└── frontend/                        # Vue.js 前端
    ├── index.html
    ├── vite.config.js
    └── src/
        ├── main.js
        ├── App.vue
        ├── router/index.js
        ├── api/seating.js           # API 呼叫層
        ├── store/seating.js         # Pinia 狀態管理
        ├── views/SeatingView.vue    # 主頁面
        ├── components/
        │   ├── FloorSection.vue     # 樓層區塊
        │   └── SeatCard.vue         # 單一座位卡片
        └── assets/main.css
```

## 技術

| 層次 | 技術 |
|------|------|
| 前端 | Vue.js 3 + Pinia + Vue Router + Axios + Vite |
| 後端 | Spring Boot 3.2 + Spring JDBC + Validation |
| 資料庫 | MySQL 8.0 + Stored Procedures |
| 建置工具 | Maven |

## 啟動步驟

### 1. 建立資料庫

```bash
mysql -u root -p < backend/DB/01_DDL.sql
mysql -u root -p < backend/DB/02_DML.sql
mysql -u root -p < backend/DB/03_StoredProcedures.sql
```

### 2. 設定資料庫連線

編輯 `backend/src/main/resources/application.yml`，
修改 `spring.datasource.username` 與 `password`，
或透過環境變數設定：

```bash
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
```

### 3. 啟動後端

```bash
cd backend
mvn spring-boot:run
# 後端啟動於 http://localhost:8080
```

### 4. 啟動前端

```bash
cd frontend
npm install
npm run dev
# 前端啟動於 http://localhost:5173
```

## RESTful API

| Method | Path | 說明 |
|--------|------|------|
| GET | `/api/floors` | 取得所有樓層座位（含佔用資訊） |
| GET | `/api/employees` | 取得員工清單 |
| POST | `/api/seats` | 批次更新座位 |

### POST /api/seats 請求範例

```json
{
  "changes": [
    { "floorSeatSeq": 3, "empId": "12006" },
    { "floorSeatSeq": 7, "empId": null }
  ]
}
```

## 安全性措施

- **SQL Injection 防護**：所有資料庫存取透過 `SimpleJdbcCall` + Stored Procedure，
  以 Prepared Statement 方式傳遞參數，不拼接 SQL 字串。
- **XSS 防護**：
  - 後端：`XssUtil.escape()` 可對輸出字串進行 HTML Escape。
  - 前端：Vue.js `{{ }}` 模板預設進行 HTML Escape，避免使用 `v-html`。
- **Validation**：`@Valid` + JSR-303 在 Controller 層驗證員編格式（固定 5 碼數字）。
- **Transaction**：`SeatingService.assignSeats()` 加上 `@Transactional`，
  多筆資料異動時任一失敗即全部回滾。

## 業務規則

1. 每位員工只能佔用一個座位（資料庫 `UNIQUE` 約束 + 業務層檢查）。
2. 選擇員工後點選空位 → 座位變橘色（請選擇），尚未寫入資料庫。
3. 點選已佔用座位 → 清除為空位（pending 狀態）。
4. 按「送出」後才將所有變更一次寫入資料庫。
