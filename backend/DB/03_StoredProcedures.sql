-- ============================================================
-- 玉山銀行 員工座位系統 - Stored Procedures
-- ============================================================

USE seating_db;

DELIMITER $$

-- ------------------------------------------------------------
-- SP_GET_ALL_SEATS
-- 取得所有座位（含佔用員工資訊）
-- ------------------------------------------------------------
DROP PROCEDURE IF EXISTS SP_GET_ALL_SEATS$$
CREATE PROCEDURE SP_GET_ALL_SEATS()
BEGIN
    SELECT
        sc.FLOOR_SEAT_SEQ,
        sc.FLOOR_NO,
        sc.SEAT_NO,
        e.EMP_ID,
        e.NAME AS EMP_NAME
    FROM SeatingChart sc
    LEFT JOIN Employee e ON e.FLOOR_SEAT_SEQ = sc.FLOOR_SEAT_SEQ
    ORDER BY sc.FLOOR_NO, sc.SEAT_NO;
END$$

-- ------------------------------------------------------------
-- SP_GET_SEATS_BY_FLOOR
-- 依樓層取得座位
-- ------------------------------------------------------------
DROP PROCEDURE IF EXISTS SP_GET_SEATS_BY_FLOOR$$
CREATE PROCEDURE SP_GET_SEATS_BY_FLOOR(IN p_floor_no INT)
BEGIN
    SELECT
        sc.FLOOR_SEAT_SEQ,
        sc.FLOOR_NO,
        sc.SEAT_NO,
        e.EMP_ID,
        e.NAME AS EMP_NAME
    FROM SeatingChart sc
    LEFT JOIN Employee e ON e.FLOOR_SEAT_SEQ = sc.FLOOR_SEAT_SEQ
    WHERE sc.FLOOR_NO = p_floor_no
    ORDER BY sc.SEAT_NO;
END$$

-- ------------------------------------------------------------
-- SP_GET_ALL_EMPLOYEES
-- 取得所有員工
-- ------------------------------------------------------------
DROP PROCEDURE IF EXISTS SP_GET_ALL_EMPLOYEES$$
CREATE PROCEDURE SP_GET_ALL_EMPLOYEES()
BEGIN
    SELECT
        EMP_ID,
        NAME,
        EMAIL,
        FLOOR_SEAT_SEQ
    FROM Employee
    ORDER BY EMP_ID;
END$$

-- ------------------------------------------------------------
-- SP_GET_EMPLOYEE_BY_ID
-- 依員編查詢單一員工
-- ------------------------------------------------------------
DROP PROCEDURE IF EXISTS SP_GET_EMPLOYEE_BY_ID$$
CREATE PROCEDURE SP_GET_EMPLOYEE_BY_ID(IN p_emp_id CHAR(5))
BEGIN
    SELECT
        EMP_ID,
        NAME,
        EMAIL,
        FLOOR_SEAT_SEQ
    FROM Employee
    WHERE EMP_ID = p_emp_id;
END$$

-- ------------------------------------------------------------
-- SP_UPDATE_EMPLOYEE_SEAT
-- 更新員工座位（NULL 表示清除）
-- ------------------------------------------------------------
DROP PROCEDURE IF EXISTS SP_UPDATE_EMPLOYEE_SEAT$$
CREATE PROCEDURE SP_UPDATE_EMPLOYEE_SEAT(
    IN p_emp_id        CHAR(5),
    IN p_floor_seat_seq INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    UPDATE Employee
    SET FLOOR_SEAT_SEQ = p_floor_seat_seq,
        UPDATE_TIME    = NOW()
    WHERE EMP_ID = p_emp_id;

    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '找不到指定員工';
    END IF;
END$$

-- ------------------------------------------------------------
-- SP_CLEAR_SEAT
-- 清除座位（將佔用此座位的員工 FLOOR_SEAT_SEQ 設為 NULL）
-- ------------------------------------------------------------
DROP PROCEDURE IF EXISTS SP_CLEAR_SEAT$$
CREATE PROCEDURE SP_CLEAR_SEAT(IN p_floor_seat_seq INT)
BEGIN
    UPDATE Employee
    SET FLOOR_SEAT_SEQ = NULL,
        UPDATE_TIME    = NOW()
    WHERE FLOOR_SEAT_SEQ = p_floor_seat_seq;
END$$

DELIMITER ;
