package com.construct.constructAthens.Employees.Employee_dependencies;

import java.time.LocalDate;
import java.time.LocalTime;

public interface EmployeeTimeProjection {
    LocalTime getTime();
    LocalDate getDate();
    long getTotalTime();
    double getLatitude();
    double getLongitude();
    LocalTime getCheckIn();
    LocalTime getCheckOut();
}