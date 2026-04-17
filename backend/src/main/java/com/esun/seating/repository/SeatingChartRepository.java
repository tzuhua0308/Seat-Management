package com.esun.seating.repository;

import com.esun.seating.model.SeatingChart;
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

/**
 * 資料層：透過 Stored Procedure 存取 SeatingChart 資料表。
 * 使用 JdbcTemplate + SimpleJdbcCall 防止 SQL Injection。
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SeatingChartRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 呼叫 SP：取得所有樓層座位（含佔用員工資訊）。
     */
    @SuppressWarnings("unchecked")
    public List<SeatingChart> findAllWithOccupant() {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_GET_ALL_SEATS")
                .returningResultSet("seats", new SeatingChartRowMapper());

        Map<String, Object> result = call.execute();
        return (List<SeatingChart>) result.get("seats");
    }

    /**
     * 呼叫 SP：依樓層取得座位。
     */
    @SuppressWarnings("unchecked")
    public List<SeatingChart> findByFloor(int floorNo) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_GET_SEATS_BY_FLOOR")
                .returningResultSet("seats", new SeatingChartRowMapper());

        Map<String, Object> result = call.execute(Map.of("p_floor_no", floorNo));
        return (List<SeatingChart>) result.get("seats");
    }

    /**
     * 呼叫 SP：清除指定座位的員工佔用。
     */
    public void clearSeat(int floorSeatSeq) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_CLEAR_SEAT");
        call.execute(Map.of("p_floor_seat_seq", floorSeatSeq));
        log.debug("Cleared seat seq={}", floorSeatSeq);
    }

    // RowMapper
    private static class SeatingChartRowMapper implements RowMapper<SeatingChart> {
        @Override
        public SeatingChart mapRow(ResultSet rs, int rowNum) throws SQLException {
            return SeatingChart.builder()
                    .floorSeatSeq(rs.getInt("FLOOR_SEAT_SEQ"))
                    .floorNo(rs.getInt("FLOOR_NO"))
                    .seatNo(rs.getString("SEAT_NO"))
                    .occupiedByEmpId(rs.getString("EMP_ID"))
                    .occupiedByName(rs.getString("EMP_NAME"))
                    .build();
        }
    }
}
