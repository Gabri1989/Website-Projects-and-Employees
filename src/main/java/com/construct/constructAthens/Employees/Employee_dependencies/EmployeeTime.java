package com.construct.constructAthens.Employees.Employee_dependencies;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
public class EmployeeTime {
    @Id
    @Column(name = "employee_id")
    private UUID employeeId;
    @Column(name = "date")
    private Date date;
    @Column(name = "accumulated_time")
    private Double accumulatedTime;
}
