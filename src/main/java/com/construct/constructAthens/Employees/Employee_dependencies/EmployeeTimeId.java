package com.construct.constructAthens.Employees.Employee_dependencies;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;
@Data
@NoArgsConstructor
@Embeddable
public class EmployeeTimeId implements Serializable {
    @Column(name = "employee_id")
    private UUID employeeId;

    @Column(name = "date")
    private LocalDate date;

}
