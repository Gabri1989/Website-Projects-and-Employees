package com.construct.constructAthens.Employees.Employee_dependencies;

import java.time.LocalDate;

public interface EmployeeTimeProjection {
    LocalDate getDate();
    Double getTotalTime();
}