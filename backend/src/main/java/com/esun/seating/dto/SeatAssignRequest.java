package com.esun.seating.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class SeatAssignRequest {

    @NotEmpty(message = "座位異動清單不可為空")
    private List<SeatChange> changes;

    @Data
    public static class SeatChange {

        /** 座位序號 */
        private Integer floorSeatSeq;

        /**
         * 員工編號 (5 碼)；若為 null 表示清除此座位
         */
        @Pattern(regexp = "^[0-9]{5}$", message = "員編必須為 5 碼數字")
        private String empId;
    }
}
