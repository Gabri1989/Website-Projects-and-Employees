package com.construct.constructAthens.Holidays;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;
@NoArgsConstructor
@Data
@Entity
public class Holiday {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID holidayID;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "message")
    private String message;

    @Column(name = "status", nullable = false)
    private String status;
}