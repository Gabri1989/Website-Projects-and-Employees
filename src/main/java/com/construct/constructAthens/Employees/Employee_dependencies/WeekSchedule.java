package com.construct.constructAthens.Employees.Employee_dependencies;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class WeekSchedule {
    private DayOfWeek day;
    private LocalTime startSchedule;
    private LocalTime endSchedule;
}
