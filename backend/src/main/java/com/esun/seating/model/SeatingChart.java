package com.esun.seating.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatingChart {

    /** 序號 (Primary Key) */
    private Integer floorSeatSeq;

    /** 樓層編號 */
    private Integer floorNo;

    /** 座位編號 */
    private String seatNo;

    /** 佔用此座位的員工編號 (來自 Employee join，非資料表欄位) */
    private String occupiedByEmpId;

    /** 佔用員工姓名 (join 結果，非資料表欄位) */
    private String occupiedByName;
}
