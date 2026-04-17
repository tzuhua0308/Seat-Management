package com.esun.seating.repository;

import com.esun.seating.model.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 資料層：透過 Stored Procedure 存取 Employee 資料表。
 * 所有參數均透過 Prepared Statement 傳遞，防止 SQL Injection。
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 呼叫 SP：取得所有員工清單。
     */
    @SuppressWarnings("unchecked")
    public List<Employee> findAll() {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_GET_ALL_EMPLOYEES")
                .returningResultSet("employees", new EmployeeRowMapper());

        Map<String, Object> result = call.execute();
        return (List<Employee>) result.get("employees");
    }

    /**
     * 呼叫 SP：依員編查詢員工。
     */
    @SuppressWarnings("unchecked")
    public Optional<Employee> findById(String empId) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_GET_EMPLOYEE_BY_ID")
                .returningResultSet("employee", new EmployeeRowMapper());

        Map<String, Object> result = call.execute(Map.of("p_emp_id", empId));
        List<Employee> list = (List<Employee>) result.get("employee");
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    /**
     * 呼叫 SP：更新員工座位（單筆）。
     * p_emp_id: 員工編號
     * p_floor_seat_seq: 座位序號（null 代表清除）
     */
    public void updateEmployeeSeat(String empId, Integer floorSeatSeq) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_UPDATE_EMPLOYEE_SEAT");
        call.execute(Map.of(
                "p_emp_id", empId,
                "p_floor_seat_seq", floorSeatSeq != null ? floorSeatSeq : java.sql.Types.NULL
        ));
        log.debug("Updated seat for emp={} -> seq={}", empId, floorSeatSeq);
    }

    // RowMapper
    private static class EmployeeRowMapper implements RowMapper<Employee> {
        @Override
        public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Employee.builder()
                    .empId(rs.getString("EMP_ID"))
                    .name(rs.getString("NAME"))
                    .email(rs.getString("EMAIL"))
                    .floorSeatSeq(rs.getObject("FLOOR_SEAT_SEQ") != null
                            ? rs.getInt("FLOOR_SEAT_SEQ") : null)
                    .build();
        }
    }
}
