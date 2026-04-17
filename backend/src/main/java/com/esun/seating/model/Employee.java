package com.esun.seating.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    /** 員編 (Primary Key, 固定 5 碼) */
    private String empId;

    /** 員工姓名 */
    private String name;

    /** 員工電子郵件 */
    private String email;

    /** 座位序號 (Foreign Key -> SeatingChart.floorSeatSeq) */
    private Integer floorSeatSeq;
}
