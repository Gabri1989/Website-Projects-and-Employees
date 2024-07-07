package com.construct.constructAthens.Holidays;

import com.construct.constructAthens.Employees.Employee_dependencies.HolidayWithEmployeeDetailsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface HolidayRepository extends JpaRepository<Holiday, UUID> {
    @Query("SELECT h FROM Holiday h WHERE h.status = 'Reject' AND h.startDate >= :startDate AND h.endDate <= :endDate")
    List<Holiday> findAvailableEmployees(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    @Query("SELECT h FROM Holiday h WHERE h.status = 'Approved' AND (h.startDate <= :endDate AND h.endDate >= :startDate)")
    List<Holiday> findHolidaysByEmployees(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT new com.construct.constructAthens.Employees.Employee_dependencies.HolidayWithEmployeeDetailsDTO(h.holidayID, h.employeeId,h.startDate, h.endDate, e.fullname, e.imageURL) " +
            "FROM Holiday h JOIN Employee e ON h.employeeId = e.id " +
            "WHERE h.status = 'reject' AND h.startDate >= :startDate AND h.endDate <= :endDate")
    List<HolidayWithEmployeeDetailsDTO> findAvailableEmployeesWithDetails(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Holiday> findListHolidaysByEmployeeId(UUID employeeId);
    Optional<Holiday> findHolidayByHolidayID(UUID id);

}