-- ============================================================
-- 玉山銀行 員工座位系統 - DDL
-- DB: seating_db  |  Charset: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS seating_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE seating_db;

-- ------------------------------------------------------------
-- Table: SeatingChart (樓層座位表)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS SeatingChart (
    FLOOR_SEAT_SEQ  INT          NOT NULL AUTO_INCREMENT COMMENT '座位序號 (PK)',
    FLOOR_NO        INT          NOT NULL               COMMENT '樓層編號',
    SEAT_NO         VARCHAR(10)  NOT NULL               COMMENT '座位編號',
    CREATE_TIME     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT PK_SEATINGCHART PRIMARY KEY (FLOOR_SEAT_SEQ),
    CONSTRAINT UQ_FLOOR_SEAT   UNIQUE (FLOOR_NO, SEAT_NO)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='樓層座位表';

-- ------------------------------------------------------------
-- Table: Employee (員工資料表)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Employee (
    EMP_ID          CHAR(5)      NOT NULL               COMMENT '員工編號 (PK, 固定 5 碼)',
    NAME            VARCHAR(50)  NOT NULL               COMMENT '員工姓名',
    EMAIL           VARCHAR(100) NOT NULL               COMMENT '員工電子郵件',
    FLOOR_SEAT_SEQ  INT          NULL                   COMMENT '座位序號 (FK -> SeatingChart)',
    CREATE_TIME     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT PK_EMPLOYEE        PRIMARY KEY (EMP_ID),
    CONSTRAINT FK_EMP_SEAT        FOREIGN KEY (FLOOR_SEAT_SEQ)
        REFERENCES SeatingChart(FLOOR_SEAT_SEQ)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    CONSTRAINT UQ_EMP_SEAT        UNIQUE (FLOOR_SEAT_SEQ),   -- 每個座位只能一人
    CONSTRAINT CHK_EMP_ID_LENGTH  CHECK (CHAR_LENGTH(EMP_ID) = 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='員工資料表';
