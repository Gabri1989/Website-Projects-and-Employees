package com.construct.constructAthens.Employees.Employee_dependencies;

import java.time.LocalDate;
import java.time.LocalTime;

public interface EmployeeTimeProjection {
    LocalDate getDate();
    Double getTotalTime();
    LocalTime getTime();
    Double getLatitude();
    Double getLongitude();
    LocalTime getCheckIn();
    LocalTime getCheckOut();
}