package com.construct.constructAthens.Employees.Employee_dependencies;

import java.time.LocalDate;

public interface EmployeeProjection {
    LocalDate getDate();
    Double getTotalTime();
}
