package com.esun.seating.service;

import com.esun.seating.common.exception.BusinessException;
import com.esun.seating.dto.FloorSeatsResponse;
import com.esun.seating.dto.SeatAssignRequest;
import com.esun.seating.model.Employee;
import com.esun.seating.model.SeatingChart;
import com.esun.seating.repository.EmployeeRepository;
import com.esun.seating.repository.SeatingChartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 業務層：座位管理核心邏輯。
 * - 每位員工只能佔用一個座位。
 * - 同時異動多筆資料使用 @Transactional 確保一致性。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeatingService {

    private final SeatingChartRepository seatingChartRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * 取得所有樓層座位，依樓層分組回傳。
     */
    public List<FloorSeatsResponse> getAllFloorSeats() {
        List<SeatingChart> all = seatingChartRepository.findAllWithOccupant();

        Map<Integer, List<SeatingChart>> grouped = all.stream()
                .collect(Collectors.groupingBy(SeatingChart::getFloorNo));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> FloorSeatsResponse.builder()
                        .floorNo(e.getKey())
                        .seats(e.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 取得所有員工清單（供下拉選單使用）。
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * 批次更新座位。
     * 需同時異動多筆 Employee 資料，使用 @Transactional 避免資料錯亂。
     *
     * 業務規則：
     *   1. empId == null → 清除該座位
     *   2. empId != null → 先清除該員工舊座位，再指派新座位
     *   3. 同一員工不可同時被指派多個座位
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignSeats(SeatAssignRequest request) {
        List<SeatAssignRequest.SeatChange> changes = request.getChanges();

        // 驗證：同一員工不能在此次變更中出現超過一次
        Map<String, Long> empCount = changes.stream()
                .filter(c -> c.getEmpId() != null)
                .collect(Collectors.groupingBy(SeatAssignRequest.SeatChange::getEmpId,
                        Collectors.counting()));

        empCount.forEach((empId, count) -> {
            if (count > 1) {
                throw new BusinessException("員工 " + empId + " 不可同時指派多個座位");
            }
        });

        for (SeatAssignRequest.SeatChange change : changes) {
            int seq = change.getFloorSeatSeq();
            String empId = change.getEmpId();

            if (empId == null) {
                // 清除座位：找出目前佔用者並清除其 FLOOR_SEAT_SEQ
                log.info("Clearing seat seq={}", seq);
                seatingChartRepository.clearSeat(seq);
            } else {
                // 驗證員工存在
                Employee emp = employeeRepository.findById(empId)
                        .orElseThrow(() -> new BusinessException("員工編號 " + empId + " 不存在"));

                // 若該員工已有其他座位，先清除舊座位
                if (emp.getFloorSeatSeq() != null && emp.getFloorSeatSeq() != seq) {
                    log.info("Clearing old seat seq={} for emp={}", emp.getFloorSeatSeq(), empId);
                    seatingChartRepository.clearSeat(emp.getFloorSeatSeq());
                }

                // 指派新座位
                log.info("Assigning seat seq={} to emp={}", seq, empId);
                employeeRepository.updateEmployeeSeat(empId, seq);
            }
        }
    }
}
