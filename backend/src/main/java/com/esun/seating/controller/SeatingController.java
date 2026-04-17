package com.esun.seating.controller;

import com.esun.seating.common.response.ApiResponse;
import com.esun.seating.dto.FloorSeatsResponse;
import com.esun.seating.dto.SeatAssignRequest;
import com.esun.seating.model.Employee;
import com.esun.seating.service.SeatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 展示層：RESTful API 端點。
 * XSS 防護：所有輸入經 @Valid 驗證，輸出由前端 Vue.js 的 {{ }} 模板自動 Escape。
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SeatingController {

    private final SeatingService seatingService;

    /**
     * GET /api/floors
     * 取得所有樓層座位資訊。
     */
    @GetMapping("/floors")
    public ApiResponse<List<FloorSeatsResponse>> getAllFloors() {
        log.info("GET /api/floors");
        return ApiResponse.ok(seatingService.getAllFloorSeats());
    }

    /**
     * GET /api/employees
     * 取得員工清單（供下拉選單使用）。
     */
    @GetMapping("/employees")
    public ApiResponse<List<Employee>> getAllEmployees() {
        log.info("GET /api/employees");
        return ApiResponse.ok(seatingService.getAllEmployees());
    }

    /**
     * POST /api/seats
     * 批次更新座位。
     */
    @PostMapping("/seats")
    public ApiResponse<Void> assignSeats(@Valid @RequestBody SeatAssignRequest request) {
        log.info("POST /api/seats, changes={}", request.getChanges().size());
        seatingService.assignSeats(request);
        return ApiResponse.ok("座位更新成功", null);
    }
}
