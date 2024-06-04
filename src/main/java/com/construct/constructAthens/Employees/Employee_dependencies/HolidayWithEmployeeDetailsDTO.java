package com.construct.constructAthens.Employees.Employee_dependencies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HolidayWithEmployeeDetailsDTO {
    private UUID holidayID;
    private UUID employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String employeeFullname;
    private String employeeImageUrl;

}
