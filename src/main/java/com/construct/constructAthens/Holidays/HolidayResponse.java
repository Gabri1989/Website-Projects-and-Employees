package com.construct.constructAthens.Holidays;

import lombok.Data;

import java.util.List;
@Data
public class HolidayResponse {
    private List<Holiday> holidays;
    private Integer maxIndex;
}
