package com.construct.constructAthens.Employees.Employee_dependencies;

import lombok.Data;

import java.time.LocalTime;
@Data
public class EmployeeCheckInOut {
    private LocalTime checkIn;
    private LocalTime checkOut;

    public EmployeeCheckInOut(LocalTime checkIn, LocalTime checkOut) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

}
