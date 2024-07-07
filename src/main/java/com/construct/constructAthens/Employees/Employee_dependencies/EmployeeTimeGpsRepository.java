package com.construct.constructAthens.Employees.Employee_dependencies;

import com.construct.constructAthens.Projects.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface EmployeeTimeGpsRepository extends JpaRepository<EmployeeTime, UUID> {
   @Query("SELECT e.date AS date, SUM(e.accumulatedTime) AS totalTime, e.time AS time, e.Latitude AS latitude, e.Longitude AS longitude, e.checkIn AS checkIn, e.checkOut AS checkOut " +
           "FROM EmployeeTime e " +
           "WHERE e.employeeId = :employeeId AND e.projectId = :projectId AND EXTRACT(MONTH FROM e.date) = :month " +
           "GROUP BY e.date, e.time, e.Latitude, e.Longitude, e.checkIn, e.checkOut")
   List<EmployeeTimeProjection> getAccumulatedTimePerDay(@Param("employeeId") UUID employeeId, @Param("projectId") UUID projectId, @Param("month") int month);

   List<EmployeeTime> findByEmployeeIdAndProjectId(UUID employeeId, UUID projectId);
   EmployeeTime findByEmployeeIdAndDateAndProjectId(UUID employeeId, LocalDate date, UUID projectId);

}
