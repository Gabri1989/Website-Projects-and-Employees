package com.construct.constructAthens.Employees.Employee_dependencies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface EmployeeTimeGpsRepository extends JpaRepository<EmployeeTime, UUID> {
   @Query("SELECT e.date AS date, SUM(e.accumulatedTime) AS totalTime FROM EmployeeTime e WHERE e.employeeId = :employeeId AND EXTRACT(MONTH FROM e.date) = :month GROUP BY e.date")
   List<EmployeeProjection> getAccumulatedTimePerMonth(UUID employeeId, int month);
   @Query("SELECT e.date AS date, SUM(e.accumulatedTime) AS totalTime, e.time AS time, e.Latitude AS latitude, e.Longitude AS longitude " +
           "FROM EmployeeTime e " +
           "WHERE e.employeeId = :employeeId AND EXTRACT(MONTH FROM e.date) = :month " +
           "GROUP BY e.date, e.time, e.Latitude, e.Longitude")
   List<EmployeeTimeProjection> getAccumulatedTimePerDay(@Param("employeeId") UUID employeeId, @Param("month") int month);


   EmployeeTime findByEmployeeIdAndDate(UUID employeeid, LocalDate currentDate);
}
