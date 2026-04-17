package com.esun.seating.dto;

import com.esun.seating.model.SeatingChart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorSeatsResponse {

    private Integer floorNo;
    private List<SeatingChart> seats;
}
