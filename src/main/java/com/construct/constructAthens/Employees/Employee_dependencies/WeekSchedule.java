package com.construct.constructAthens.Employees.Employee_dependencies;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class WeekSchedule {
    private DayOfWeek day;
    private String startSchedule;
    private String endSchedule;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeekSchedule that = (WeekSchedule) o;
        return day == that.day &&
                Objects.equals(startSchedule, that.startSchedule) &&
                Objects.equals(endSchedule, that.endSchedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, startSchedule, endSchedule);
    }
}