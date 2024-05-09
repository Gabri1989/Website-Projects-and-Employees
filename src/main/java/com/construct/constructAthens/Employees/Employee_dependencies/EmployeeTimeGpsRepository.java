package com.construct.constructAthens.Employees.Employee_dependencies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface EmployeeTimeGpsRepository extends JpaRepository<EmployeeTime, UUID> {
   @Query("SELECT e.date AS date, SUM(e.accumulatedTime) AS totalTime FROM EmployeeTime e WHERE e.employeeId = :employeeId AND EXTRACT(MONTH FROM e.date) = :month GROUP BY e.date")
   List<EmployeeTimeProjection> getAccumulatedTimePerDay(UUID employeeId, int month);

   List<EmployeeTime> findByEmployeeIdAndDateBetween(UUID employeeId, Date startDate, Date endDate);
}
