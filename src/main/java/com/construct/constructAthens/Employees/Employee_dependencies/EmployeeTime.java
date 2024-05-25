package com.construct.constructAthens.Employees.Employee_dependencies;

import com.construct.constructAthens.Employees.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
//@IdClass(EmployeeTimeId.class)
public class EmployeeTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "employee_id")
    private UUID employeeId;
    @Column(name = "project_id")
    private UUID projectId;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "accumulated_time")
    private Double accumulatedTime=0.0;
    @Value("${app.time}")
    private LocalTime time;
    private Double Latitude;
    private Double Longitude;
    private LocalTime checkIn;
    private LocalTime checkOut;

    public EmployeeTime(UUID employeeId,UUID projectId, LocalDate currentDate,LocalTime time, Double accumulatedTime, Double latitude, Double longitude) {
        this.employeeId = employeeId;
        this.projectId = projectId;
        this.date = currentDate;
        this.time=time;
        this.accumulatedTime = accumulatedTime;
        this.Latitude = latitude;
        this.Longitude = longitude;
    }
}
